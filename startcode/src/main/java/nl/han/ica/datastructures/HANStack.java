package nl.han.ica.datastructures;

import java.util.LinkedList;

public class HANStack<T> implements IHANStack<T> {
    private final LinkedList<T> stack;

    public HANStack() {
        stack = new LinkedList<>();
    }

    @Override
    public void push(T value) {
        int size = stack.size();
        stack.add(size, value);
    }

    @Override
    public T pop() {
        int size = stack.size();
        T value = stack.get(size - 1);
        stack.remove(size - 1);
        return value;
    }

    @Override
    public T peek() {
        int size = stack.size();
        return stack.get(size - 1);
    }
}
