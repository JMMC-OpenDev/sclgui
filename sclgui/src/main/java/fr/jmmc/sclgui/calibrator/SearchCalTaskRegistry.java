/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.sclgui.calibrator;

import fr.jmmc.jmcs.gui.task.Task;
import fr.jmmc.jmcs.gui.task.TaskRegistry;

/**
 * This class describes the SearchCal tasks associated with SwingWorker(s) and their execution order
 * @author bourgesl
 */
public final class SearchCalTaskRegistry extends TaskRegistry {

    /** task registry singleton */
    private final static SearchCalTaskRegistry instance;

    /* SearchCal tasks */
    /** Load task */
    public final static Task TASK_LOAD;

    /**
     * Static initializer to define tasks and their child tasks
     */
    static {
        // create the task registry singleton :
        instance = new SearchCalTaskRegistry();

        // create tasks :
        TASK_LOAD = new Task("Load");

        // register tasks :
        instance.addTask(TASK_LOAD);
    }

    /**
     * Singleton pattern for the registry itself
     * @return registry instance
     */
    public static TaskRegistry getInstance() {
        return instance;
    }

    /**
     * Protected constructor
     */
    private SearchCalTaskRegistry() {
        super();
    }
}
