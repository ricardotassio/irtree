/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xxl.util;

import java.util.NoSuchElementException;
import xxl.util.statistics.Count;
import xxl.core.collections.containers.ConstrainedDecoratorContainer;
import xxl.core.collections.containers.Container;

/**
 *
 * @author joao
 */
public class CountContainer extends ConstrainedDecoratorContainer{
    private final Count count;

    public CountContainer (Container container, Count count) {
        super(container);
        this.count = count;
    }

    public void reset () {
        count.reset();
    }

    public Object get (Object id, boolean unfix) throws NoSuchElementException {
        Object object = super.get(id, unfix);

        count.update(1);

        return object;
    }
}
