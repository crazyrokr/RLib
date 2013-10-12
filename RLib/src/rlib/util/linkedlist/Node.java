package rlib.util.linkedlist;

import rlib.util.pools.Foldable;

/**
 * Реазилация узла для связанного списка.
 * 
 * @author Ronn
 */
public final class Node<E> implements Foldable {

	/** содержимый итем */
	private E item;

	/** ссылка на предыдущий узел */
	private Node<E> prev;
	/** ссылка на след. узел */
	private Node<E> next;

	@Override
	public void finalyze() {
		item = null;
		prev = null;
		next = null;
	}

	/**
	 * @return хранимый итем.
	 */
	public E getItem() {
		return item;
	}

	/**
	 * @return следующий узел.
	 */
	public Node<E> getNext() {
		return next;
	}

	/**
	 * @return предыдущий узел.
	 */
	public Node<E> getPrev() {
		return prev;
	}

	@Override
	public void reinit() {
	}

	/**
	 * @param item хранимый итем.
	 */
	public void setItem(final E item) {
		this.item = item;
	}

	/**
	 * @param next следующий узел.
	 */
	public void setNext(final Node<E> next) {
		this.next = next;
	}

	/**
	 * @param prev предыдущий узел.
	 */
	public void setPrev(final Node<E> prev) {
		this.prev = prev;
	}
}
