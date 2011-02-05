/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConnectorBase;

import java.util.Vector;

/**
 *
 * @author Rafael
 */
public class ConnectorSpecification {

    private Vector properties_ = new Vector();

    public void AddProperty(int[] Properties) {
        for (int y = 0; y < Properties.length; y++) {
            properties_.addElement(new Integer(Properties[y]));
        }
    }

    public boolean HasPropterty(int Property) {
        if (properties_.contains(new Integer(Property))) {
            return true;
        } else {
            return false;
        }
    }
}
