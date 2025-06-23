package network.simulation.test.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import network.simulation.test.Model.IModel;
import network.simulation.test.View.IView;

public class MainController implements IViewController, IModelController {
    private IView view;
    private IModel model;
    private final Map<String, Consumer<String[]>> actionMap = new HashMap<>();
    
    public MainController(IModel model, IView view) {
        this.view = view;

        actionMap.put("newProject", args -> view.newProject(args[0]));
        actionMap.put("openProject", args -> view.openProject(args[0]));
        actionMap.put("saveProject", args -> view.saveProject(args[0]));
        actionMap.put("saveProjectAs", args -> view.saveProjectAs(args[0]));
        actionMap.put("closeProject", args -> view.closeProject(args[0]));
        actionMap.put("addStandardNetwork", args -> {
            model.addStandardNetwork();
            view.addNetworkToTree(model.getNetworks().get(0));
        });
        actionMap.put("addCustomNetwork", args -> {
            model.createNetwork(args[0], args[1]);
            view.addNetworkToTree(args[0]);
        });
        actionMap.put("addStandardNode", args -> {
            model.addStandardNode();
            view.addNodeToTree(model.getUnassignedNodes().get(model.getUnassignedNodes().size()-1).getName());
        });
        actionMap.put("addCustomNode", args -> {
            model.createNode(args[0], args[1]);
            view.addNodeToTree(args[0]);
        });

        actionMap.put("deleteNode", args -> model.deleteNode(args[0]));
        actionMap.put("assignNodeToNetwork", args -> model.assignNode(args[0], args[1]));
        //actionMap.put("editNode", args -> model.renameNode(args[0], args[1]));

        view.setController(this);
    }

    public void onClick(String action, String... args) {
        Consumer<String[]> handler = actionMap.get(action);
        if (handler != null) {
            handler.accept(args);
        } else {
            System.out.println("Unknown action: " + action);
        }
    }
}
