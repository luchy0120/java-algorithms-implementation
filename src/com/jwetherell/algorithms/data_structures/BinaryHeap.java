package com.jwetherell.algorithms.data_structures;


/**
 * A binary heap is a heap data structure created using a binary tree. It can be seen as a binary tree with two additional constraints:
 *   1) The shape property: the tree is a complete binary tree; that is, all levels of the tree, except possibly the last one (deepest) are fully filled, 
 *      and, if the last level of the tree is not complete, the nodes of that level are filled from left to right.
 *   2) The heap property: each node is greater than or equal to each of its children according to a comparison predicate defined for the data structure.
 * 
 * http://en.wikipedia.org/wiki/Binary_heap
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class BinaryHeap<T extends Comparable<T>> {

    private Node<T> root = null;
    private int size = 0;
    
    public enum TYPE { MIN,MAX };
    private TYPE type = TYPE.MIN;

    public BinaryHeap() { 
        root = null;
        size = 0;
    }
    
    public BinaryHeap(TYPE type) { 
        this();
        this.type = type;
    }

    public BinaryHeap(T[] nodes) { 
        this();
        populate(nodes);
    }
    
    public BinaryHeap(T[] nodes, TYPE type) { 
        this(type);
        populate(nodes);
    }

    private void populate(T[] nodes) {
        for (T node : nodes) {
            add(new Node<T>(null,node));
        }
    }
    
    private int[] getDirections(int index) {
        int directionsSize = (int)(Math.log10(index+1)/Math.log10(2))-1;
        int[] directions = null;
        if (directionsSize>0) {
            directions = new int[directionsSize];
            int i = directionsSize-1;
            while (i>=0) {
                index = (index-1)/2;
                directions[i--] = (index>0 && index % 2==0)?1:0; // 0=left, 1=right
            }
        }
        return directions;
    }

    public void add(T value) {
        add(new Node<T>(null,value));
    }
    
    private void add(Node<T> newNode) {
        if (root==null) {
            root = newNode;
            size++;
            return;
        }

        Node<T> node = root;
        int[] directions = getDirections(size); // size == index of new node
        if (directions!=null && directions.length>0) {
            for (int d : directions) {
                if (d==0) {
                    //Go left
                    node = node.lesser;
                } else {
                    //Go right
                    node = node.greater;
                }
            }
        }
        if (node.lesser==null) {
            node.lesser = newNode;
        } else {
            node.greater = newNode;
        }

        newNode.parent = node;
        size++;
        heapUp(newNode);
    }

    public T removeRoot() {
        T result = null;
        if (root!=null) {
            result = root.value;
            remove(root.value);
        }
        return result;
    }
    
    private void remove(T value) {
        //Find the last node
        int[] directions = getDirections(size-1); // Directions to the last node
        Node<T> lastNode = root;
        if (directions!=null && directions.length>0) {
            for (int d : directions) {
                if (d==0) {
                    //Go left
                    lastNode = lastNode.lesser;
                } else {
                    //Go right
                    lastNode = lastNode.greater;
                }
            }
        }
        if (lastNode.greater!=null) {
            lastNode = lastNode.greater;
        } else if (lastNode.lesser!=null) {
            lastNode = lastNode.lesser;
        }

        //Could not find the last node, strange
        if (lastNode==null) return;
        
        //Find the node to replace
        Node<T> nodeToRemove = getNode(root, value);

        //Could not find the node to remove, strange
        if (nodeToRemove==null) return;

        //Replace the node to remove with the last node
        Node<T> lastNodeParent = lastNode.parent;
        if (lastNodeParent!=null) {
            if (lastNodeParent.lesser!=null && lastNodeParent.lesser.equals(lastNode)) {
                lastNodeParent.lesser = null;
            } else {
                lastNodeParent.greater = null;
            }
        }
        
        if (lastNode.equals(nodeToRemove)) {
            if (lastNode.equals(root)) root = null;
            size--;
        } else {        
            Node<T> nodeToRemoveParent = nodeToRemove.parent;
            if (nodeToRemoveParent!=null) {
                if (nodeToRemoveParent.lesser!=null && nodeToRemoveParent.lesser.equals(nodeToRemove)) {
                    nodeToRemoveParent.lesser = lastNode;
                } else {
                    nodeToRemoveParent.greater = lastNode;
                }
            } else {
                root = lastNode;
            }
            lastNode.parent = nodeToRemoveParent;
            lastNode.lesser = nodeToRemove.lesser;
            if (lastNode.lesser!=null) lastNode.lesser.parent = lastNode;
            lastNode.greater = nodeToRemove.greater;
            if (lastNode.greater!=null) lastNode.greater.parent = lastNode;
            size--;
            heapDown(root);
        }
    }
    
    private Node<T> getNode(Node<T> startingNode, T value) {
        Node<T> result = null;
        if (startingNode!=null && startingNode.value == value) {
            result = startingNode;
        } else if (startingNode!=null && startingNode.value != value) {
            Node<T> left = startingNode.lesser;
            if (left!=null) {
                result = getNode(left, value);
                if (result!=null) return result;
            }
            Node<T> right = startingNode.greater;
            if (right!=null) {
                result = getNode(right, value);
                if (result!=null) return result;
            }
        }
        return result;
    }

    private void heapUp(Node<T> node) {
        while (node != null) {
            Node<T> parent = node.parent;
            
            int compare = (type == TYPE.MIN)?-1:1;
            if (parent!=null && node.value.compareTo(parent.value) == compare) {
                //Node is less than parent, switch node with parent
                Node<T> grandParent = parent.parent;
                Node<T> parentLeft = parent.lesser;
                Node<T> parentRight = parent.greater;
                
                parent.lesser = node.lesser;
                if (parent.lesser!=null) parent.lesser.parent = parent;
                parent.greater = node.greater;
                if (parent.greater!=null) parent.greater.parent = parent;
                
                if (parentLeft!=null && parentLeft.equals(node)) {
                    node.lesser = parent;
                    node.greater = parentRight;
                    if (parentRight!=null) parentRight.parent = node;
                } else {
                    node.greater = parent;
                    node.lesser = parentLeft;
                    if (parentLeft!=null) parentLeft.parent = node;
                }
                parent.parent = node;

                if (grandParent==null) {
                    //New root.
                    node.parent = null;
                    root = node;
                } else {
                    Node<T> grandLeft = grandParent.lesser;
                    if (grandLeft!=null && grandLeft.equals(parent)) {
                        grandParent.lesser = node;
                    } else {
                        grandParent.greater = node;
                    }
                    node.parent = grandParent;
                }
            } else {
                node = node.parent;
            }
        }
    }

    private void heapDown(Node<T> node) {
        heapUp(node);
        Node<T> left = node.lesser;
        if (left!=null) heapDown(left);
        Node<T> right = node.greater;
        if (right!=null) heapDown(right);
    }

    private void getNodeValue(Node<T> node, int index, T[] array) {
        array[index] = node.value;
        index = (index*2)+1;

        Node<T> left = node.lesser;
        if (left!=null) getNodeValue(left,index,array);
        Node<T> right = node.greater;
        if (right!=null) getNodeValue(right,index+1,array);
    }

    @SuppressWarnings("unchecked")
    public T[] getHeap() {
        T[] nodes = (T[])new Comparable[size];
        if (root!=null) getNodeValue(root,0,nodes);
        return nodes;
    }

    public T getRootValue() {
        T result = null;
        if (root!=null) result = root.value;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (root!=null) {
            T[] heap = getHeap();
            for (T node : heap) {
                builder.append(node).append(", ");
            }
        } else {
            builder.append("Heap has no nodes.");
        }
        return builder.toString();
    }

    private static class Node<T extends Comparable<T>> {
        
        private T value = null;
        private Node<T> parent = null;
        private Node<T> lesser = null;
        private Node<T> greater = null;
        
        private Node(Node<T> parent, T value) {
            this.parent = parent;
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "value="+value+
                   " parent="+((parent!=null)?parent.value:"NULL")+
                   " lesser="+((lesser!=null)?lesser.value:"NULL")+
                   " greater="+((greater!=null)?greater.value:"NULL");
        }
    }
}
