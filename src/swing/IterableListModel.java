package swing;

import javax.swing.*;
import java.util.function.Consumer;

public class IterableListModel<E> extends DefaultListModel<E> {
    public void forEach(Consumer<E> func) {
        for (int i = 0; i < getSize(); i++)
            func.accept(get(i));
    }
}
