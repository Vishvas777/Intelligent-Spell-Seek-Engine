/* The browser-side Spellseek engine. It mirrors the Java version's main ranking rules. */
class SpellseekEngine {
  constructor() {
    this.dictionary = new Map();
    this.trie = new Trie();
    this.bkTree = new BKTree();
  }

  normalize(word) {
    return String(word ?? '').trim().replace(/[’‘]/g, "'").toLocaleLowerCase('en-US');
  }

  isValidWord(word) {
    return /^[\p{L}]+(?:'[\p{L}]+)*$/u.test(word);
  }

  addWord(rawWord, frequency = 1) {
    const word = this.normalize(rawWord);
    if (!this.isValidWord(word)) return { added: false, reason: 'invalid', word };
    const numericFrequency = Number.isFinite(Number(frequency)) && Number(frequency) > 0
      ? Number(frequency) : 1;
    const existing = this.dictionary.get(word);
    if (existing) {
      existing.frequency = Math.max(existing.frequency, numericFrequency);
      return { added: false, reason: 'exists', word, entry: existing };
    }
    const entry = { word, frequency: numericFrequency };
    this.dictionary.set(word, entry);
    this.trie.insert(entry);
    this.bkTree.insert(entry);
    return { added: true, reason: 'added', word, entry };
  }

  async loadEntries(entries, onProgress = () => {}, batchSize = 250) {
    let accepted = 0;
    let skipped = 0;
    for (let start = 0; start < entries.length; start += batchSize) {
      const end = Math.min(start + batchSize, entries.length);
      for (let index = start; index < end; index++) {
        const result = this.addWord(entries[index].word, entries[index].frequency);
        if (result.added || result.reason === 'exists') accepted++;
        else skipped++;
      }
      onProgress(end, entries.length, this.dictionary.size);
      // Yield to the browser between batches so the page remains responsive.
      await new Promise(resolve => setTimeout(resolve, 0));
    }
    return { accepted, skipped, size: this.dictionary.size };
  }

  check(word, maxDistance = 2) {
    const normalized = this.normalize(word);
    if (!normalized || !this.isValidWord(normalized)) {
      return { word: normalized, exact: false, suggestions: [], visited: 0, trace: [] };
    }
    if (this.trie.contains(normalized)) {
      return { word: normalized, exact: true, suggestions: [], visited: 0, trace: [] };
    }
    const result = this.bkTree.search(normalized, maxDistance);
    return { word: normalized, exact: false, ...result };
  }

  autocomplete(prefix, limit = 8) {
    const normalized = this.normalize(prefix);
    if (!normalized) return [];
    return this.trie.autocomplete(normalized, limit);
  }
}

window.SpellseekEngine = SpellseekEngine;
