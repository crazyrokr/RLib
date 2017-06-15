package com.ss.rlib.util.array.impl;

import static java.lang.Math.max;
import static com.ss.rlib.util.ArrayUtils.copyOf;

import com.ss.rlib.util.array.ArrayIterator;
import com.ss.rlib.util.array.ConcurrentArray;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.NoSuchElementException;

import com.ss.rlib.concurrent.atomic.AtomicInteger;
import com.ss.rlib.util.ArrayUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.UnsafeArray;

/**
 * The base concurrent implementation of the array.
 *
 * @param <E> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractConcurrentArray<E> extends AbstractArray<E> implements ConcurrentArray<E>, UnsafeArray<E> {

    private static final long serialVersionUID = -6291504312637658721L;

    /**
     * The count of elements in this array.
     */
    private final AtomicInteger size;

    /**
     * The unsafe array.
     */
    private volatile E[] array;

    /**
     * Instantiates a new Abstract concurrent array.
     *
     * @param type the type
     */
    public AbstractConcurrentArray(@NotNull final Class<E> type) {
        this(type, 10);
    }

    /**
     * Instantiates a new Abstract concurrent array.
     *
     * @param type the type
     * @param size the size
     */
    public AbstractConcurrentArray(@NotNull final Class<E> type, final int size) {
        super(type, size);

        this.size = new AtomicInteger();
    }

    @Override
    public boolean add(@NotNull final E element) {

        if (size() == array.length) {
            array = ArrayUtils.copyOf(array, Math.max(array.length >> 1, 1));
        }

        array[size.getAndIncrement()] = element;
        return true;
    }

    @Override
    public final boolean addAll(@NotNull final Array<? extends E> elements) {
        if (elements.isEmpty()) return false;

        final int current = array.length;
        final int selfSize = size();
        final int targetSize = elements.size();
        final int diff = selfSize + targetSize - current;

        if (diff > 0) {
            array = copyOf(array, max(current >> 1, diff));
        }

        processAdd(elements, selfSize, targetSize);
        return true;
    }

    @Override
    public final boolean addAll(@NotNull final Collection<? extends E> collection) {
        if (collection.isEmpty()) return false;

        final int current = array.length;
        final int diff = size() + collection.size() - current;

        if (diff > 0) {
            array = ArrayUtils.copyOf(array, Math.max(current >> 1, diff));
        }

        for (final E element : collection) unsafeAdd(element);
        return true;
    }

    @Override
    public final boolean addAll(@NotNull final E[] elements) {
        if (elements.length < 1) return false;

        final int current = array.length;
        final int selfSize = size();
        final int targetSize = elements.length;
        final int diff = selfSize + targetSize - current;

        if (diff > 0) {
            array = copyOf(array, max(current >> 1, diff));
        }

        processAdd(elements, selfSize, targetSize);
        return true;
    }

    @NotNull
    @Override
    public final E[] array() {
        return array;
    }

    @Override
    public void prepareForSize(final int size) {

        final int current = array.length;
        final int selfSize = size();
        final int diff = selfSize + size - current;

        if (diff > 0) {
            array = ArrayUtils.copyOf(array, Math.max(current >> 1, diff));
        }
    }

    @NotNull
    @Override
    public final E fastRemove(final int index) {

        if (index < 0 || index >= size()) {
            throw new NoSuchElementException();
        }

        final int newSize = size.decrementAndGet();
        final E old = array[index];

        array[index] = array[newSize];
        array[newSize] = null;

        return old;
    }

    @NotNull
    @Override
    public final E get(final int index) {

        if (index < 0 || index >= size()) {
            throw new NoSuchElementException();
        }

        return array[index];
    }

    @NotNull
    @Override
    public final ArrayIterator<E> iterator() {
        return new FinalArrayIterator<>(this);
    }

    @Override
    public final void set(final int index, @NotNull final E element) {

        if (index < 0 || index >= size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        if (array[index] != null) {
            size.decrementAndGet();
        }

        array[index] = element;
        size.incrementAndGet();
    }

    @Override
    protected final void setArray(@NotNull final E[] array) {
        this.array = array;
    }

    @Override
    protected final void setSize(final int size) {
        this.size.getAndSet(size);
    }

    @Override
    public final int size() {
        return size.get();
    }

    @NotNull
    @Override
    public final E slowRemove(final int index) {

        if (index < 0 || index >= size()) {
            throw new NoSuchElementException();
        }

        final int length = size();
        final int numMoved = length - index - 1;

        final E old = array[index];

        if (numMoved > 0) {
            System.arraycopy(array, index + 1, array, index, numMoved);
        }

        array[size.decrementAndGet()] = null;
        return old;
    }

    @Override
    public final AbstractConcurrentArray<E> trimToSize() {

        final int size = size();
        if (size == array.length) return this;

        array = ArrayUtils.copyOfRange(array, 0, size);
        return this;
    }

    @Override
    public boolean unsafeAdd(@NotNull E object) {
        array[size.getAndIncrement()] = object;
        return true;
    }

    @Override
    public E unsafeGet(final int index) {
        return array[index];
    }

    @Override
    public void unsafeSet(int index, @NotNull E element) {

        if (array[index] != null) {
            size.decrementAndGet();
        }

        array[index] = element;
        size.incrementAndGet();
    }

    /**
     * Process add.
     *
     * @param elements   the elements
     * @param selfSize   the self size
     * @param targetSize the target size
     */
    protected void processAdd(final Array<? extends E> elements, final int selfSize, final int targetSize) {
        // если надо срау большой массив добавить, то лучше черзе нативный метод
        if (targetSize > SIZE_BIG_ARRAY) {
            System.arraycopy(elements.array(), 0, array, selfSize, targetSize);
            size.set(selfSize + targetSize);
        } else {
            // если добавляемый массив небольшой, можно и обычным способом
            // внести
            for (final E element : elements.array()) {
                if (element == null) break;
                unsafeAdd(element);
            }
        }
    }

    /**
     * Process add.
     *
     * @param elements   the elements
     * @param selfSize   the self size
     * @param targetSize the target size
     */
    protected void processAdd(final E[] elements, final int selfSize, final int targetSize) {
        // если надо срау большой массив добавить, то лучше черзе нативный метод
        if (targetSize > SIZE_BIG_ARRAY) {
            System.arraycopy(elements, 0, array, selfSize, targetSize);
            size.set(selfSize + targetSize);
        } else {
            // если добавляемый массив небольшой, можно и обычным способом
            // внести
            for (final E element : elements) {
                if (element == null) break;
                unsafeAdd(element);
            }
        }
    }

    @Override
    public UnsafeArray<E> asUnsafe() {
        return this;
    }

    @NotNull
    @Override
    public AbstractConcurrentArray<E> clone() throws CloneNotSupportedException {
        final AbstractConcurrentArray<E> clone = (AbstractConcurrentArray<E>) super.clone();
        clone.array = ArrayUtils.copyOf(array, size());
        return clone;
    }
}