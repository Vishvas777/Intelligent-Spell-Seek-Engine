/* Trie: exact-word lookup, prefix autocomplete, and lookup-path visualization. */
class TrieNode {
  constructor() {
    this.children = new Map();
    this.entry = null;
  }
}

class Trie {
  constructor() {
    this.root = new TrieNode();
    this.wordCount = 0;
  }

  insert(entry) {
    let node = this.root;
    for (const character of entry.word) {
      if (!node.children.has(character)) node.children.set(character, new TrieNode());
      node = node.children.get(character);
    }
    if (!node.entry) this.wordCount++;
    node.entry = entry;
  }

  findNode(prefix) {
    let node = this.root;
    for (const character of prefix) {
      node = node.children.get(character);
      if (!node) return null;
    }
    return node;
  }

  contains(word) {
    const node = this.findNode(word);
    return Boolean(node && node.entry);
  }

  autocomplete(prefix, limit = 10) {
    const start = this.findNode(prefix);
    if (!start) return [];
    const found = [];
    const stack = [{ node: start, word: prefix }];
    while (stack.length) {
      const current = stack.pop();
      if (current.node.entry) found.push(current.node.entry);
      for (const [character, child] of current.node.children) {
        stack.push({ node: child, word: current.word + character });
      }
    }
    found.sort((a, b) => b.frequency - a.frequency || a.word.localeCompare(b.word));
    return found.slice(0, limit);
  }

  describePath(word) {
    const lines = ['Root'];
    let node = this.root;
    let built = '';
    if (!word) return lines.concat('Type a word to trace its character path.').join('\n');
    for (const character of word) {
      built += character;
      node = node && node.children.get(character);
      lines.push(`${built}${node ? (node.entry ? '  ✓ (complete word)' : '  →') : '  ✗ (path ends here)'}`);
      if (!node) break;
    }
    if (node && !node.entry) lines.push('Prefix exists, but is not a complete dictionary word.');
    if (node && node.entry) lines.push('Exact dictionary match.');
    return lines.join('\n');
  }
}

window.Trie = Trie;
