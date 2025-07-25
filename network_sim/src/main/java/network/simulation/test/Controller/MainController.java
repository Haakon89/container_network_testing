package network.simulation.test.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import network.simulation.test.Model.IModelController;
import network.simulation.test.View.IView;

public class MainController implements IControllerView, IControllerModel {
    private IView view;
    private IModelController model;
    private final Map<String, Consumer<String[]>> actionMap = new HashMap<>();
    
    public MainController(IModelController model, IView view) {
        this.model = model;
        this.view = view;
    
        actionMap.put("newProject", args -> view.newProject(args[0], args[1]));
        actionMap.put("closeProject", args -> view.closeProject());
        actionMap.put("openProject", args -> model.loadModel(args[0]));
        actionMap.put("saveProject", args -> model.saveModel());
        actionMap.put("saveProjectAs", args -> model.saveModelAs(args[0], args[1]));
        actionMap.put("addStandardNetwork", args -> model.addStandardNetwork());
        actionMap.put("addCustomNetwork", args -> model.createNetwork(args[0], args[1]));
        actionMap.put("createCustomDevice", args -> model.createCustomDevice(args[0], args[1]));
        actionMap.put("createDevice", args -> model.createDevice(args[0]));
        actionMap.put("buildProject", args -> model.buildProject());
        actionMap.put("deleteDevice", args -> model.deleteDevice(args[0], args[1]));
        actionMap.put("assignDeviceToNetwork", args -> model.assignDevice(args[0], args[1]));
        actionMap.put("editDevice", args -> model.editDevice(args[0], args[1], args[2], args[3], args[4]));

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
