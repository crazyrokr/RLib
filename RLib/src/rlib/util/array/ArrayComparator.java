package rlib.util.array;

import java.util.Comparator;

/**
 * Интерфейс для реализации компаратора для {@link Array}
 *
 * @author Ronn
 */
public interface ArrayComparator<T> extends Comparator<T> {

	@Override
	public default int compare(final T first, final T second) {

		if(first == null) {
			return 1;
		} else if(second == null) {
			return -1;
		}

		return compareImpl(first, second);
	}

	/**
	 * Сравнение 2х объектов.
	 */
	public int compareImpl(T first, T second);
}
