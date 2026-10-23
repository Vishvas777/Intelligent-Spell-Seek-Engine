/* BK-tree and Levenshtein distance for approximate spelling lookup. */
function levenshteinDistance(first, second) {
  if (first === second) return 0;
  if (first.length < second.length) [first, second] = [second, first];
  const m = first.length;
  const n = second.length;
  let previous = Array.from({ length: n + 1 }, (_, index) => index);
  let current = new Array(n + 1);

  for (let i = 1; i <= m; i++) {
    current[0] = i;
    const firstChar = first.charAt(i - 1);
    for (let j = 1; j <= n; j++) {
      const substitution = previous[j - 1] + (firstChar === second.charAt(j - 1) ? 0 : 1);
      current[j] = Math.min(previous[j] + 1, current[j - 1] + 1, substitution);
    }
    [previous, current] = [current, previous];
  }
  return previous[n];
}

class BKNode {
  constructor(entry) {
    this.entry = entry;
    this.children = new Map();
  }
}

class BKTree {
  constructor() {
    this.root = null;
    this.size = 0;
  }

  insert(entry) {
    if (!this.root) {
      this.root = new BKNode(entry);
      this.size++;
      return;
    }
    let node = this.root;
    while (true) {
      const distance = levenshteinDistance(entry.word, node.entry.word);
      if (distance === 0) {
        node.entry = entry;
        return;
      }
      if (!node.children.has(distance)) {
        node.children.set(distance, new BKNode(entry));
        this.size++;
        return;
      }
      node = node.children.get(distance);
    }
  }

  search(query, maxDistance) {
    if (!this.root) return { suggestions: [], visited: 0, trace: [] };
    const suggestions = [];
    const trace = [];
    const stack = [this.root];
    let visited = 0;
    let pruned = 0;

    while (stack.length) {
      const node = stack.pop();
      const distance = levenshteinDistance(query, node.entry.word);
      visited++;
      if (distance <= maxDistance) {
        suggestions.push({
          word: node.entry.word,
          frequency: node.entry.frequency,
          distance
        });
      }
      const lower = Math.max(0, distance - maxDistance);
      const upper = distance + maxDistance;
      let eligibleChildren = 0;
      for (const [edgeDistance, child] of node.children) {
        if (edgeDistance >= lower && edgeDistance <= upper) {
          stack.push(child);
          eligibleChildren++;
        }
      }
      if (trace.length < 12) {
        trace.push(`${node.entry.word}: distance ${distance}; explore edge ${lower}..${upper} (${eligibleChildren} branch${eligibleChildren === 1 ? '' : 'es'})`);
      }
      pruned += node.children.size - eligibleChildren;
    }

    suggestions.sort((a, b) => a.distance - b.distance || b.frequency - a.frequency || a.word.localeCompare(b.word));
    return { suggestions, visited, pruned, trace };
  }
}

window.BKTree = BKTree;
window.levenshteinDistance = levenshteinDistance;
