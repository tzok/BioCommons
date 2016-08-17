package darrylbu.component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An extension of JMenuItem that doesn't close the menu when selected.
 *
 * @author Darryl
 */
public class StayOpenMenuItem extends JMenuItem {
    private static final long serialVersionUID = 1L;
    static MenuElement[] path;

    {
        getModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (getModel().isArmed() && isShowing()) {
                    StayOpenMenuItem.path =
                            MenuSelectionManager.defaultManager()
                                                .getSelectedPath();
                }
            }
        });
    }

    /**
     * @see JMenuItem#JMenuItem()
     */
    public StayOpenMenuItem() {
        super();
    }

    /**
     * @see JMenuItem#JMenuItem(javax.swing.Action)
     */
    public StayOpenMenuItem(Action a) {
        super(a);
    }

    /**
     * @see JMenuItem#JMenuItem(javax.swing.Icon)
     */
    public StayOpenMenuItem(Icon icon) {
        super(icon);
    }

    /**
     * @see JMenuItem#JMenuItem(java.lang.String)
     */
    public StayOpenMenuItem(String text) {
        super(text);
    }

    /**
     * @see JMenuItem#JMenuItem(java.lang.String, javax.swing.Icon)
     */
    public StayOpenMenuItem(String text, Icon icon) {
        super(text, icon);
    }

    /**
     * @see JMenuItem#JMenuItem(java.lang.String, int)
     */
    public StayOpenMenuItem(String text, int mnemonic) {
        super(text, mnemonic);
    }

    /**
     * Overridden to reopen the menu.
     *
     * @param pressTime the time to "hold down" the button, in milliseconds
     */
    @Override
    public void doClick(int pressTime) {
        super.doClick(pressTime);
        MenuSelectionManager.defaultManager()
                            .setSelectedPath(StayOpenMenuItem.path);
    }
}
