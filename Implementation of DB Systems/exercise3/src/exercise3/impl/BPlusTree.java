package exercise3.impl;

import java.util.*;

public class BPlusTree<K extends Comparable<? super K>, V> {
    static final int ENTRY_COUNT = 8; // Adjust the maximum number of entries per node

    // Record class represents key-value pairs stored in LeafNodes
    final class Record implements Comparable<Map.Entry<K, V>>, Map.Entry<K, V> {
        final K key;
        V value;

        Record(K key) {
            this.key = key;
        }

        Record(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(Map.Entry<K, V> o) {
            return this.key.compareTo(o.getKey());
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            return this.value = value;
        }
    }

    // Abstract Node class
    abstract static class Node {
        abstract boolean isLeaf();

        abstract Node split();
    }

    // IndexNode class
    final class IndexNode extends Node {
        final List<K> keys = new ArrayList<>(ENTRY_COUNT);
        final List<Node> children = new ArrayList<>(ENTRY_COUNT + 1);

        @Override
        boolean isLeaf() {
            return false;
        }

        @Override
        Node split() {
            int mid = keys.size() / 2;

            IndexNode sibling = new IndexNode();
            sibling.keys.addAll(keys.subList(mid + 1, keys.size()));
            sibling.children.addAll(children.subList(mid + 1, children.size()));

            keys.subList(mid, keys.size()).clear();
            children.subList(mid + 1, children.size()).clear();

            return sibling;
        }

        void insertSplitKey(K key, Node left, Node right) {
            int pos = Collections.binarySearch(keys, key);
            if (pos < 0) pos = -(pos + 1);

            keys.add(pos, key);
            children.set(pos, left);
            children.add(pos + 1, right);

            if (keys.size() > ENTRY_COUNT) {
                Node sibling = split();
                if (this == root) {
                    IndexNode newRoot = new IndexNode();
                    newRoot.keys.add(keys.remove(keys.size() - 1));
                    newRoot.children.add(this);
                    newRoot.children.add(sibling);
                    root = newRoot;
                    System.out.println("Root split. New root created.");
                }
            }
        }
    }

    // LeafNode class
    final class LeafNode extends Node {
        final List<Record> records = new ArrayList<>(ENTRY_COUNT);
        LeafNode next;

        @Override
        Node split() {
            int mid = records.size() / 2;

            LeafNode sibling = new LeafNode();
            sibling.records.addAll(records.subList(mid, records.size()));
            records.subList(mid, records.size()).clear();

            sibling.next = next;
            next = sibling;

            return sibling;
        }

        @Override
        boolean isLeaf() {
            return true;
        }

        V insert(K key, V value) {
            int pos = Collections.binarySearch(records, new Record(key));

            if (pos >= 0) {
                return records.get(pos).setValue(value);
            } else {
                pos = -(pos + 1);
                records.add(pos, new Record(key, value));
                System.out.println("Inserted key: " + key + ", at position: " + pos);

                if (records.size() > ENTRY_COUNT) {
                    Node sibling = split();
                    if (this == root) {
                        IndexNode newRoot = new IndexNode();
                        newRoot.keys.add(((LeafNode) sibling).records.get(0).key);
                        newRoot.children.add(this);
                        newRoot.children.add(sibling);
                        root = newRoot;
                        System.out.println("Root split. New root created.");
                    }
                }
                return null;
            }
        }
    }

    private Node root;

    public BPlusTree() {
        root = new LeafNode();
    }

    public V insert(K key, V value) {
        System.out.println("Inserting key: " + key + ", value: " + value);

        if (root.isLeaf()) {
            return ((LeafNode) root).insert(key, value);
        } else {
            return insertIntoInternal((IndexNode) root, key, value);
        }
    }

    private V insertIntoInternal(IndexNode node, K key, V value) {
        int pos = Collections.binarySearch(node.keys, key);
        if (pos >= 0) pos++;
        else pos = -(pos + 1);

        Node child = node.children.get(pos);
        V oldValue;

        if (child.isLeaf()) {
            LeafNode leafChild = (LeafNode) child;
            oldValue = leafChild.insert(key, value);

            if (leafChild.records.size() > ENTRY_COUNT) {
                Node sibling = leafChild.split();
                node.insertSplitKey(((LeafNode) sibling).records.get(0).key, leafChild, sibling);
            }
        } else {
            IndexNode indexChild = (IndexNode) child;
            oldValue = insertIntoInternal(indexChild, key, value);

            if (indexChild.keys.size() > ENTRY_COUNT) {
                Node sibling = indexChild.split();
                node.insertSplitKey(((IndexNode) sibling).keys.get(0), indexChild, sibling);
            }
        }

        return oldValue;
    }

    public V pointQuery(K key) {
        System.out.println("Querying key: " + key);

        Node current = root;
        while (!current.isLeaf()) {
            IndexNode indexNode = (IndexNode) current;
            int pos = Collections.binarySearch(indexNode.keys, key);

            if (pos >= 0) {
                pos++;
            } else {
                pos = -(pos + 1);
            }

            current = indexNode.children.get(pos);
        }

        LeafNode leafNode = (LeafNode) current;
        int pos = Collections.binarySearch(leafNode.records, new Record(key));

        V result = (pos >= 0) ? leafNode.records.get(pos).getValue() : null;
        System.out.println("Query result for key " + key + ": " + result);
        return result;
    }

    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>();
        for (int i = 0; i < 100; i++) {
            tree.insert(i, "Value" + i);
            tree.pointQuery(i);
        }
    }
}
