package SortedList;


/**
 * Implementation of a SortedList using a SinglyLinkedList
 * @author Fernando J. Bermudez & Juan O. Lopez
 * @author Jonathan Rivera Chico
 * @version 2.0
 * @since 10/16/2021
 */
public class SortedLinkedList<E extends Comparable<? super E>> extends AbstractSortedList<E> {

	@SuppressWarnings("unused")
	private static class Node<E> {

		private E value;
		private Node<E> next;

		public Node(E value, Node<E> next) {
			this.value = value;
			this.next = next;
		}

		public Node(E value) {
			this(value, null); // Delegate to other constructor
		}

		public Node() {
			this(null, null); // Delegate to other constructor
		}

		public E getValue() {
			return value;
		}

		public void setValue(E value) {
			this.value = value;
		}

		public Node<E> getNext() {
			return next;
		}

		public void setNext(Node<E> next) {
			this.next = next;
		}

		public void clear() {
			value = null;
			next = null;
		}				
	} // End of Node class

	
	private Node<E> head; // First DATA node (This is NOT a dummy header node)
	
	public SortedLinkedList() {
		head = null;
		currentSize = 0;
	}

	/**
	 * Takes an element and adds it into the list from lowest to highest
	 * @param e - Element to be inserted
	 */
	@Override
	public void add(E e) {
		/* Special case: Be careful when the new value is the smallest */
		Node<E> newNode = new Node<>(e);
		Node<E> prevNode = head;
		if(this.head == null){
			head = newNode; // adds if there is no elements in the list
			currentSize++;
			return;
		}

		//Special case if the element is less than the head of the list it just adds it as the head
		if(newNode.getValue().compareTo(head.getValue()) < 0){
			Node<E> stored = head; // stores the value of the previous head
			head = newNode; // sets the new element as the head
			head.setNext(stored);//sets the previous head as the next of the new head
			currentSize++;
			return;
		}
		Node<E> curNode = prevNode.getNext();
		while(prevNode != null){
			//If the element is the biggest element to be inserted, it adds it to the final of the lsit
			if(curNode == null){
				prevNode.setNext(newNode);
				break;
			}
			//Finds the element that is bigger than the one that has to be inserted
			// and inserts it between the bigger node and the previous node
			if(newNode.getValue().compareTo(curNode.getValue()) < 0){
				newNode.setNext(curNode);
				prevNode.setNext(newNode);
				break;
			}
			curNode = curNode.getNext();
			prevNode = prevNode.getNext();
		}
		currentSize++;

	}

	/**
	 * Removes the specified item of the list
	 * @param e - Element to be removed
	 * @return true if removed, false otherwise
	 */
	@Override
	public boolean remove(E e) {
		Node<E> prevNode = head;
		Node<E> curNode = this.head.getNext();

		//If the head is the element that has to be deleted, then
		//it just replaces the head as the next node
		if(prevNode.getValue().equals(e)){
			head = curNode;
			currentSize--;
			return true;
		}

		while(curNode != null){
			if(curNode.getValue().equals(e)){
				prevNode.setNext(curNode.getNext()); //sets the next of the previous node as the next of the to be deleted node
				currentSize--;
				return true;
			}
			prevNode = prevNode.getNext();
			curNode = curNode.getNext();
		}
		return false;
	}

	/**
	 * Removes an item in the specified index
	 * @param index - Index of item to be removed
	 * @return the removed element
	 */
	@Override
	public E removeIndex(int index) {
		E val = this.get(index); //Uses the get method to find the element in the index
		if(remove(val))return val; // Uses the remove method to remove the element
		return null;
	}

	/**
	 * Takes an element and finds the first index of where the element is located
	 * @param e - Element to be inserted
	 * @return the index of the element
	 */
	@Override
	public int firstIndex(E e) {
		int target = 0;
		Node <E> cur = head;
		while(cur != null){
			if(cur.getValue().equals(e)){
				return target; // If the value is found, it returns the target counter
			}
			target++;
			cur = cur.getNext();
		}
		return -1;
	}

	@Override
	/**
	 * Takes an index and finds the element in the index
	 * @param index - Index of the item you want
	 * @return the item in the index
	 */
	public E get(int index) {
		Node<E> cur = head;
		//Uses the index as the counter, and if it reaches 0, it returns the element
		while(cur != null){
			if(index == 0) return cur.getValue();
			index--;
			cur = cur.getNext();
		}
		return null;
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public E[] toArray() {
		int index = 0;
		E[] theArray = (E[]) new Comparable[size()]; // Cannot use Object here
		for(Node<E> curNode = this.head; index < size() && curNode  != null; curNode = curNode.getNext(), index++) {
			theArray[index] = curNode.getValue();
		}
		return theArray;
	}

}
