/* UI wiring. Works offline when served from this folder (for example with VS Code Live Server). */
(() => {
  'use strict';

  const engine = new SpellseekEngine();
  const input = document.getElementById('search-input');
  const distanceInput = document.getElementById('max-dist');
  const prefixInput = document.getElementById('prefix-input');
  const prefixButton = document.getElementById('prefix-btn');
  const prefixResults = document.getElementById('prefix-results');
  const sentenceInput = document.getElementById('sentence-input');
  const sentenceButton = document.getElementById('check-sentence-btn');
  const sentenceResults = document.getElementById('sentence-results');
  const results = document.getElementById('suggestions-container');
  const trieView = document.getElementById('trie-vis');
  const bkView = document.getElementById('bk-vis');
  const newWordInput = document.getElementById('new-word');
  const newFreqInput = document.getElementById('new-freq');
  const addWordButton = document.getElementById('add-word-btn');
  const workbenchStatus = document.getElementById('workbench-status');
  const STORAGE_KEY = 'spellseek_custom_words_v1';
  let ready = false;
  let searchTimer = null;

  const fallbackWords = `about above across act action add after again against age ago air allow almost along already also always am an and animal another answer any anyone anything apple apply application appreciate are area around as ask at away back be because become been before begin behind believe best better between big book both boy bring build business but by call came can car care case change check child city class clear close code computer consider could country course create day different do does done door down each early earth easy end enough even ever every example eye face fact family far feel few find first follow food for form found four friend from full game general get give go good got great group grow had hand happen has have he head hear help her here high him his history home house how i idea if important in include information input inside instead into is it its job just keep kind know language large last later learn least leave left less let life light like line list little live local long look made make man many map mark may me mean measure men might mind miss more most move much music must my name near need never new next night no not note number of off often old on once one only open or order other our out over own page part people place plan play point possible power present problem program programming project provide public put question quick quite read real reason receive right run said same saw say school science search second see seem sentence serve set several she should show side simple since small so some something sound spell spelling spellseek start state still stop story student study such system take talk tell test than that the their them then there these they thing think this those through time to together too tree trie try turn two under understand until up use useful user usually value very want was water way we well went were what when where which while white who whole why will with word work world would write year yes yet you your hello java dictionary frequency suggestion autocomplete typo incorrect correct algorithm`.split(/\s+/);

  function setStatus(message, kind = 'info') {
    let status = document.getElementById('app-status');
    if (!status) {
      status = document.createElement('p');
      status.id = 'app-status';
      status.className = 'app-status';
      document.querySelector('.container').insertBefore(status, document.querySelector('.nav-tabs'));
    }
    status.textContent = message;
    status.dataset.kind = kind;
  }

  function parseDictionary(text) {
    const entries = [];
    for (const line of text.split(/\r?\n/)) {
      const trimmed = line.trim();
      if (!trimmed || trimmed.startsWith('#')) continue;
      const parts = trimmed.split(/\s+/);
      entries.push({ word: parts[0], frequency: Number(parts[1]) > 0 ? Number(parts[1]) : 1 });
    }
    return entries;
  }

  function readCustomWords() {
    try {
      const saved = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
      return Array.isArray(saved) ? saved : [];
    } catch (_) {
      return [];
    }
  }

  function saveCustomWord(word, frequency) {
    const saved = readCustomWords().filter(item => engine.normalize(item.word) !== word);
    saved.push({ word, frequency });
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(saved));
      return true;
    } catch (_) {
      return false;
    }
  }

  function addSectionHeading(parent, text) {
    const heading = document.createElement('p');
    heading.className = 'result-heading';
    heading.textContent = text;
    parent.appendChild(heading);
  }

  function addSuggestion(parent, entry, label = '') {
    const item = document.createElement('div');
    item.className = 'suggestion-item';
    const word = document.createElement('span');
    word.className = 'suggestion-word';
    word.textContent = entry.word;
    const meta = document.createElement('span');
    meta.className = 'suggestion-meta';
    meta.textContent = label || `frequency ${Number(entry.frequency).toLocaleString()}`;
    item.append(word, meta);
    parent.appendChild(item);
  }

  function renderResults() {
    const raw = input.value.trim();
    results.replaceChildren();
    if (!ready) {
      const p = document.createElement('p');
      p.className = 'muted';
      p.textContent = 'Loading dictionary and building search indexes…';
      results.appendChild(p);
      return;
    }
    if (!raw) {
      const p = document.createElement('p');
      p.className = 'muted';
      p.textContent = 'Start typing to view corrections and autocomplete suggestions.';
      results.appendChild(p);
      updateVisualizers('');
      return;
    }

    const normalized = engine.normalize(raw);
    const maxDistance = Math.max(1, Math.min(5, Number(distanceInput.value) || 2));
    const prefixMatches = engine.autocomplete(normalized, 6);
    const check = engine.check(normalized, maxDistance);

    if (check.exact) {
      const correct = document.createElement('p');
      correct.className = 'success-message';
      correct.textContent = `✓ “${normalized}” is in the dictionary.`;
      results.appendChild(correct);
    } else if (!engine.isValidWord(normalized)) {
      const p = document.createElement('p');
      p.className = 'muted';
      p.textContent = 'Enter one word using letters, with optional apostrophes inside the word.';
      results.appendChild(p);
    } else if (check.suggestions.length) {
      addSectionHeading(results, `Closest corrections (searched ${check.visited.toLocaleString()} tree nodes)`);
      check.suggestions.slice(0, 5).forEach(item => {
        addSuggestion(results, item, `edit distance ${item.distance} · frequency ${item.frequency.toLocaleString()}`);
      });
    } else {
      const p = document.createElement('p');
      p.className = 'muted';
      p.textContent = `No suggestions found within edit distance ${maxDistance}. Try a larger threshold or add the word in Dictionary Workbench.`;
      results.appendChild(p);
    }

    if (prefixMatches.length) {
      addSectionHeading(results, 'Autocomplete');
      prefixMatches.forEach(item => addSuggestion(results, item));
    }
    updateVisualizers(normalized, maxDistance, check);
  }

  function renderPrefixResults() {
    prefixResults.replaceChildren();
    if (!ready) {
      prefixResults.textContent = 'Dictionary is still loading…';
      return;
    }
    const prefix = engine.normalize(prefixInput.value);
    if (!prefix || !engine.isValidWord(prefix)) {
      prefixResults.textContent = 'Enter a prefix containing letters (and optionally an internal apostrophe).';
      return;
    }
    const matches = engine.autocomplete(prefix, 20);
    if (!matches.length) {
      prefixResults.textContent = `No dictionary words start with “${prefix}”.`;
      return;
    }
    addSectionHeading(prefixResults, `${matches.length} matching word${matches.length === 1 ? '' : 's'} (up to 20 shown)`);
    matches.forEach(entry => addSuggestion(prefixResults, entry));
  }

  function preserveCase(original, replacement) {
    if (original === original.toLocaleUpperCase('en-US')) return replacement.toLocaleUpperCase('en-US');
    if (original && original[0] === original[0].toLocaleUpperCase('en-US')) {
      return replacement.charAt(0).toLocaleUpperCase('en-US') + replacement.slice(1);
    }
    return replacement;
  }

  function checkSentence() {
    sentenceResults.replaceChildren();
    if (!ready) {
      sentenceResults.textContent = 'Please wait until the dictionary has loaded.';
      return;
    }
    const sentence = sentenceInput.value;
    if (!sentence.trim()) {
      sentenceResults.textContent = 'Enter a sentence first.';
      return;
    }
    const maxDistance = Math.max(1, Math.min(5, Number(distanceInput.value) || 2));
    const wordPattern = /[\p{L}]+(?:['’][\p{L}]+)*/gu;
    const issues = [];
    const corrected = sentence.replace(wordPattern, original => {
      const normalized = engine.normalize(original);
      if (engine.trie.contains(normalized)) return original;
      const check = engine.check(normalized, maxDistance);
      if (!check.suggestions.length) {
        issues.push({ original, replacement: null, options: [] });
        return original;
      }
      const replacement = preserveCase(original, check.suggestions[0].word);
      issues.push({ original, replacement, options: check.suggestions.slice(0, 5) });
      return replacement;
    });

    const proposed = document.createElement('p');
    proposed.className = 'sentence-proposal';
    const label = document.createElement('strong');
    label.textContent = 'Suggested sentence: ';
    proposed.append(label, document.createTextNode(corrected));
    sentenceResults.appendChild(proposed);

    if (!issues.length) {
      const p = document.createElement('p');
      p.className = 'success-message';
      p.textContent = '✓ No misspellings found in the current dictionary.';
      sentenceResults.appendChild(p);
      return;
    }
    addSectionHeading(sentenceResults, `Words to review (${issues.length})`);
    issues.forEach(issue => {
      const item = document.createElement('div');
      item.className = 'sentence-issue';
      const line = document.createElement('p');
      line.textContent = issue.replacement
        ? `${issue.original} → ${issue.replacement}`
        : `${issue.original} → no close suggestion found`;
      item.appendChild(line);
      if (issue.options.length) {
        const options = document.createElement('p');
        options.className = 'muted';
        options.textContent = 'Other suggestions: ' + issue.options.map(s => `${s.word} (distance ${s.distance})`).join(', ');
        item.appendChild(options);
      }
      sentenceResults.appendChild(item);
    });
    const note = document.createElement('p');
    note.className = 'muted helper-text';
    note.textContent = 'Review suggestions before using them; names and specialist terms may not be in the dictionary.';
    sentenceResults.appendChild(note);
  }

  function updateVisualizers(word, maxDistance = 2, existingCheck = null) {
    if (!ready) {
      trieView.textContent = 'Loading Trie…';
      bkView.textContent = 'Loading BK-tree…';
      return;
    }
    trieView.textContent = engine.trie.describePath(word);
    if (!word) {
      bkView.textContent = `Root: ${engine.bkTree.root ? engine.bkTree.root.entry.word : '(empty)'}\nIndexed words: ${engine.bkTree.size}\nType a word to inspect the edit-distance search.`;
      return;
    }
    const check = existingCheck || engine.check(word, maxDistance);
    if (check.exact) {
      bkView.textContent = `Exact match: ${word}\nNo approximate search needed.\nIndexed words: ${engine.bkTree.size}`;
      return;
    }
    const lines = [
      `Query: ${word}`,
      `Max edit distance: ${maxDistance}`,
      `Nodes visited: ${(check.visited || 0).toLocaleString()}`,
      `Branches pruned: ${(check.pruned || 0).toLocaleString()}`,
      'Visited nodes (first 12):',
      ...((check.trace && check.trace.length) ? check.trace : ['No tree nodes available.'])
    ];
    bkView.textContent = lines.join('\n');
  }

  async function start() {
    setStatus('Loading dictionary…');
    let entries;
    let usedFallback = false;
    try {
      const response = await fetch('dictionary.txt');
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      entries = parseDictionary(await response.text());
    } catch (error) {
      usedFallback = true;
      entries = fallbackWords.map(word => ({ word, frequency: 1 }));
    }

    const saved = readCustomWords();
    entries.push(...saved);
    const result = await engine.loadEntries(entries, (done, total, size) => {
      setStatus(`Building indexes: ${done.toLocaleString()} / ${total.toLocaleString()} entries processed · ${size.toLocaleString()} unique words`);
    });
    ready = true;
    if (usedFallback) {
      setStatus('The dictionary file could not be fetched. The built-in starter words are active; open the folder with VS Code Live Server to load dictionary.txt.', 'warning');
    } else {
      setStatus(`Ready · ${engine.dictionary.size.toLocaleString()} unique words indexed. Dictionary loads locally; no external services are used.`, 'success');
    }
    renderResults();
    if (result.skipped) console.info(`Skipped ${result.skipped} invalid dictionary entries.`);
  }

  // Tabs
  document.querySelectorAll('.tab-btn').forEach(button => {
    button.addEventListener('click', () => {
      document.querySelectorAll('.tab-btn').forEach(tab => tab.classList.toggle('active', tab === button));
      document.querySelectorAll('.tab-content').forEach(panel => panel.classList.toggle('active', panel.id === button.dataset.tab));
    });
  });

  input.addEventListener('input', () => {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(renderResults, 120);
  });
  distanceInput.addEventListener('input', renderResults);
  distanceInput.addEventListener('change', renderResults);
  prefixButton.addEventListener('click', renderPrefixResults);
  prefixInput.addEventListener('keydown', event => { if (event.key === 'Enter') renderPrefixResults(); });
  sentenceButton.addEventListener('click', checkSentence);

  addWordButton.addEventListener('click', () => {
    if (!ready) {
      workbenchStatus.textContent = 'Please wait until the dictionary has loaded.';
      return;
    }
    const word = engine.normalize(newWordInput.value);
    const frequency = Math.max(1, Number(newFreqInput.value) || 10);
    if (!engine.isValidWord(word)) {
      workbenchStatus.textContent = 'Enter a valid word (letters, with optional internal apostrophes).';
      return;
    }
    const result = engine.addWord(word, frequency);
    if (!result.added) {
      if (result.reason === 'exists') {
        workbenchStatus.textContent = `“${word}” is already in the dictionary. Its frequency was updated if the new value was higher.`;
      } else {
        workbenchStatus.textContent = 'That entry could not be added.';
      }
      return;
    }
    const saved = saveCustomWord(word, frequency);
    workbenchStatus.textContent = saved
      ? `Added “${word}” to this session and saved it in this browser.`
      : `Added “${word}” for this session, but browser storage was unavailable.`;
    newWordInput.value = '';
    setStatus(`Ready · ${engine.dictionary.size.toLocaleString()} unique words indexed.`, 'success');
    renderResults();
  });
  newWordInput.addEventListener('keydown', event => {
    if (event.key === 'Enter') addWordButton.click();
  });

  start().catch(error => {
    console.error('Spellseek failed to initialize:', error);
    setStatus('Spellseek could not initialize. Check the browser console for details.', 'warning');
    results.textContent = 'Unable to load the spell-checking engine.';
  });
})();
