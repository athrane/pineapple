/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen..
 * 
 * This file is part of Pineapple.
 * 
 * Pineapple is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * Pineapple is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public 
 * license for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.alpha.pineapple.web.zk.viewmodel;

import static com.alpha.pineapple.web.WebApplicationConstants.COMPLETED_ACTIVITY_CREATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.COMPLETED_OPERATION_GLOBALCOMMAND;
import static com.alpha.pineapple.web.WebApplicationConstants.CREATE_RESOURCE_PROPERTY_MODAL_ZUL;
import static com.alpha.pineapple.web.WebApplicationConstants.CREDENTIAL_PASSWORD_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_COMPONENT_ARGS;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_GLOBALCOMMAND_ARGS;
import static com.alpha.pineapple.web.WebApplicationConstants.NULL_PARENT_WINDOW;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_QUEUE;
import static com.alpha.pineapple.web.WebApplicationConstants.PINEAPPLE_ZK_SCOPE;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_DURATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_LOCATION;
import static com.alpha.pineapple.web.WebApplicationConstants.POST_STYLE;
import static com.alpha.pineapple.web.WebApplicationConstants.RESET_CREDENTIAL_PASSWORD_MODAL_ZUL;
import static com.alpha.pineapple.web.WebApplicationConstants.RESOURCE_PROPERTY_KEY_ARG;
import static com.alpha.pineapple.web.WebApplicationConstants.RESOURCE_PROPERTY_VALUE_ARG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Window;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.CoreException;
import com.alpha.pineapple.credential.CredentialAlreadyExitsException;
import com.alpha.pineapple.credential.CredentialNotFoundException;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Credentials;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.pineapple.model.configuration.Environments;
import com.alpha.pineapple.model.configuration.ObjectFactory;
import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.model.configuration.Resources;
import com.alpha.pineapple.plugin.PluginInitializationFailedException;
import com.alpha.pineapple.web.activity.ActivityRepository;
import com.alpha.pineapple.web.model.ResourceProperty;
import com.alpha.pineapple.web.model.RestResultMapper;
import com.alpha.pineapple.web.model.SessionState;
import com.alpha.pineapple.web.spring.rest.ConfigurationController;

/**
 * ZK view model for the environment configuration panel.
 */
public class EnvironmentConfigurationPanel {

	/**
	 * ZK Page state.
	 */
	static final String ZK_PAGESTATE_NEW_ENVIRONMENT = "NewEnvironment";

	/**
	 * ZK Page state.
	 */
	static final String ZK_PAGESTATE_NEW_RESOURCE = "NewResource";

	/**
	 * ZK Page state.
	 */
	static final String ZK_PAGESTATE_NEW_CREDENTIAL = "NewCredential";

	/**
	 * ZK Page state.
	 */
	static final String ZK_PAGESTATE_NOTHING_SELECTED = "NothingSelected";

	/**
	 * Defines value state for tree node data object.
	 */
	enum TreeNodeDataValueState {
		SYSTEM, ENVCONFIG_ROOT, ENVCONFIG_ROOT_RESOURCES, ENVCONFIG_ROOT_CREDENTIALS, ENVCONFIG_RESOURCE, ENVCONFIG_CREDENTIAL, ENVCONFIG_ENVIRONMENT, AGENTS
	}

	/**
	 * Class to encapsulate multiples type of data in a tree node.
	 */
	class TreeNodeData {
		String id; // Immutable ID
		Resource resource;
		Credential credential;
		Environment environment;
		String stringValue;
		TreeNodeDataValueState valueState;

		TreeNodeData(Resource resource) {
			this.resource = resource;
			this.valueState = TreeNodeDataValueState.ENVCONFIG_RESOURCE;
			this.id = resource.getId();
		}

		TreeNodeData(Credential credential) {
			this.credential = credential;
			this.valueState = TreeNodeDataValueState.ENVCONFIG_CREDENTIAL;
			this.id = credential.getId();
		}

		TreeNodeData(Environment environment) {
			this.environment = environment;
			this.valueState = TreeNodeDataValueState.ENVCONFIG_ENVIRONMENT;
			this.id = environment.getId();
		}

		TreeNodeData(String value, TreeNodeDataValueState state) {
			this.stringValue = value;
			this.valueState = state;
			this.id = value;
		}

		TreeNodeDataValueState getState() {
			return valueState;
		}

		Object getValue() {
			switch (valueState) {
			case ENVCONFIG_ROOT:
				return valueState;
			case ENVCONFIG_RESOURCE:
				return resource;
			case ENVCONFIG_CREDENTIAL:
				return credential;
			case ENVCONFIG_ENVIRONMENT:
				return environment;
			default:
				return "n/a";
			}
		}

		void setValue(Object value) {
		}

		String getId() {
			return id;
		}

		@Override
		public String toString() {
			return id;
		}
	}

	/**
	 * First list index.
	 */
	static final int FIRST_LIST_INDEX = 0;

	/**
	 * Null model environment. Created in the init() method, can't be constant since
	 * object factory is injected.
	 */
	Environment nullEnvironment = null;

	/**
	 * Null model resource. Created in the init() method, can't be constant since
	 * object factory is injected.
	 */
	Resource nullResource = null;

	/**
	 * Null model resource property. Created in the init() method, can't be constant
	 * since object factory is injected.
	 */
	ResourceProperty nullResourceProperty = null;

	/**
	 * Null model credential. Created in the init() method, can't be constant since
	 * object factory is injected.
	 */
	Credential nullCredential = null;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Session state.
	 */
	// @WireVariable
	// SessionState sessionState;

	/**
	 * Spring REST configuration controller.
	 */
	@WireVariable
	ConfigurationController configurationController;

	/**
	 * Message provider for I18N support.
	 */
	@WireVariable
	MessageProvider webMessageProvider;

	/**
	 * Environment Configuration model object factory.
	 */
	@WireVariable(value = "configurationModelObjectFactory")
	ObjectFactory objectFactory;

	/**
	 * REST result mapper.
	 */
	@WireVariable
	RestResultMapper restResultMapper;

	/**
	 * Session state.
	 */
	@WireVariable
	SessionState sessionState;

	/**
	 * Activity repository.
	 */
	@WireVariable
	ActivityRepository activityRepository;

	/**
	 * Selected tree node in environment/resource view.
	 */
	DefaultTreeNode<TreeNodeData> selectedTreeNode;

	/**
	 * Tree model.
	 */
	DefaultTreeModel<TreeNodeData> treeModel;

	/**
	 * Selected model environment.
	 */
	Environment selectedEnvironment = null;

	/**
	 * Selected model resource.
	 */
	Resource selectedResource = null;

	/**
	 * Selected model resource property.
	 */
	ResourceProperty selectedResourceProperty = null;

	/**
	 * Selected model credential.
	 */
	Credential selectedCredential = null;

	/**
	 * Variable to hold a temporary environment instance during its creation.
	 */
	Environment newTempEnvironment;

	/**
	 * Variable to hold a temporary resource instance during its creation.
	 */
	Resource newTempResource;

	/**
	 * Variable to hold a temporary credential instance during its creation.
	 */
	Credential newTempCredential;

	/**
	 * State variable to trigger activation of modal window for confirmation of
	 * environment deletion.
	 */
	boolean stateConfirmEnvironmentDeletion = false;

	/**
	 * State variable to trigger activation of modal window for confirmation of
	 * resource deletion.
	 */
	boolean stateConfirmResourceDeletion = false;

	/**
	 * State variable to trigger activation of modal window for confirmation of
	 * resource property deletion.
	 */
	boolean stateConfirmResourcePropertyDeletion = false;

	/**
	 * State variable to trigger activation of modal window for confirmation of
	 * credential deletion.
	 */
	boolean stateConfirmCredentialDeletion = false;

	/**
	 * New credential password.
	 */
	String newCredentialPassword = null;

	/**
	 * Initialize view model.
	 */
	@Init
	public void init() {

		// initiate null objects
		nullEnvironment = objectFactory.createEnvironment();
		nullResource = objectFactory.createResource();
		nullResourceProperty = new ResourceProperty("", "");
		nullCredential = objectFactory.createCredential();
		createViewModel();
	}

	/**
	 * Initialize view model.
	 * 
	 * Initialize environment configuration model in tree.
	 */
	void createViewModel() {

		// create root node
		DefaultTreeNode<TreeNodeData> rootNode = createTreeNode("root node", TreeNodeDataValueState.ENVCONFIG_ROOT);

		// create environment configuration nodes
		DefaultTreeNode<TreeNodeData> environmentConfigurationNode = createTreeNode(
				webMessageProvider.getMessage("ecp.environment_configuration_node_info"),
				TreeNodeDataValueState.ENVCONFIG_ROOT);
		rootNode.add(environmentConfigurationNode);
		DefaultTreeNode<TreeNodeData> resourcesNode = createTreeNode(
				webMessageProvider.getMessage("ecp.resources_node_info"),
				TreeNodeDataValueState.ENVCONFIG_ROOT_RESOURCES);
		environmentConfigurationNode.add(resourcesNode);
		DefaultTreeNode<TreeNodeData> credentialNode = createTreeNode(
				webMessageProvider.getMessage("ecp.credentials_node_info"),
				TreeNodeDataValueState.ENVCONFIG_ROOT_CREDENTIALS);
		environmentConfigurationNode.add(credentialNode);

		// create agents node
		DefaultTreeNode<TreeNodeData> agentsNode = createTreeNode(webMessageProvider.getMessage("ecp.agents_node_info"),
				TreeNodeDataValueState.AGENTS);
		rootNode.add(agentsNode);

		// create system node
		DefaultTreeNode<TreeNodeData> systemNode = createTreeNode(webMessageProvider.getMessage("ecp.system_node_info"),
				TreeNodeDataValueState.SYSTEM);
		rootNode.add(systemNode);

		// create tree model
		treeModel = new DefaultTreeModel<TreeNodeData>(rootNode);

		addResourcesToModel(resourcesNode);
		addCredentialsToModel(credentialNode);
	}

	/**
	 * Add resources to tree model.
	 * 
	 * @param resourcesNode
	 *            root tree model resource node.
	 */
	void addResourcesToModel(DefaultTreeNode<TreeNodeData> resourcesNode) {

		// get resource configuration
		Configuration resourceConfiguration = configurationController.getResourceConfiguration();
		if (resourceConfiguration == null)
			return;

		// get environments
		Environments environments = resourceConfiguration.getEnvironments();
		if (environments == null)
			return;

		// iterate over environments
		List<Environment> environmentsList = environments.getEnvironment();
		for (Environment environment : environmentsList) {

			// create new node and add it to the root tree node
			DefaultTreeNode<TreeNodeData> environmentNode = createTreeNode(environment);
			resourcesNode.add(environmentNode);

			// get resources
			Resources resources = environment.getResources();
			if (resources == null)
				continue;

			// iterate over the resources
			List<Resource> resourcesList = resources.getResource();
			for (Resource resource : resourcesList) {

				// create new node and add it to the environment tree node
				DefaultTreeNode<TreeNodeData> resourceNode = createLeafTreeNode(resource);
				environmentNode.add(resourceNode);
			}
		}
	}

	/**
	 * Add credentials to tree model.
	 * 
	 * @param credentialsNode
	 *            root tree model credential node.
	 */
	void addCredentialsToModel(DefaultTreeNode<TreeNodeData> credentialsNode) {

		// get credential configuration
		Configuration credentialConfiguration = configurationController.getCredentialConfiguration();
		if (credentialConfiguration == null)
			return;

		// get environments
		Environments environments = credentialConfiguration.getEnvironments();
		if (environments == null)
			return;

		// iterate over environments
		List<Environment> environmentsList = environments.getEnvironment();
		for (Environment environment : environmentsList) {

			// create new node and add it to the root tree node
			DefaultTreeNode<TreeNodeData> environmentNode = createTreeNode(environment);
			credentialsNode.add(environmentNode);

			// get credentials
			Credentials credentials = environment.getCredentials();
			if (credentials == null)
				continue;

			// iterate over the resources
			List<Credential> credentialsList = credentials.getCredential();
			for (Credential credential : credentialsList) {

				// create new node and add it to the environment tree node
				DefaultTreeNode<TreeNodeData> resourceNode = createLeafTreeNode(credential);
				environmentNode.add(resourceNode);
			}
		}
	}

	/**
	 * Get tree model in ZK view.
	 * 
	 * @return tree model.
	 */
	public DefaultTreeModel<TreeNodeData> getTreeModel() {
		return treeModel;
	}

	/**
	 * Set selected tree node in the ZK view.
	 * 
	 * @param treeNode
	 *            Selected environment/resource in the ZK view.
	 */
	public void setSelectedItem(DefaultTreeNode<TreeNodeData> treeNode) {
		this.selectedTreeNode = treeNode;

		// exit if selected tree node is null
		if (treeNode == null) {
			clearSelectionAndState();
			return;
		}

		// get tree node data object
		TreeNodeData dataObject = selectedTreeNode.getData();

		clearSelectionAndState();

		// handle type
		switch (dataObject.getState()) {

		case SYSTEM:
			return;

		case ENVCONFIG_ROOT:
			return;

		case ENVCONFIG_ENVIRONMENT:
			selectedEnvironment = (Environment) dataObject.getValue();
			return;

		case ENVCONFIG_RESOURCE:
			selectedResource = (Resource) dataObject.getValue();
			return;

		case ENVCONFIG_CREDENTIAL:
			selectedCredential = (Credential) dataObject.getValue();
			return;

		default:
			return;
		}
	}

	/**
	 * Clear selection and model state.
	 */
	void clearSelectionAndState() {
		selectedEnvironment = nullEnvironment;
		selectedResource = nullResource;
		selectedCredential = nullCredential;
		selectedResourceProperty = nullResourceProperty;
		stateConfirmEnvironmentDeletion = false;
		stateConfirmResourceDeletion = false;
		stateConfirmCredentialDeletion = false;
		stateConfirmResourcePropertyDeletion = false;
	}

	/**
	 * Get selected item (e.g. tree node) in ZK view.
	 * 
	 * @return selected item (e.g. tree node).
	 */
	public DefaultTreeNode<TreeNodeData> getSelectedItem() {
		return selectedTreeNode;
	}

	/**
	 * Get selected model environment in ZK view.
	 * 
	 * @return selected model environment.
	 */
	public Environment getSelectedEnvironment() {
		return selectedEnvironment;
	}

	/**
	 * Get selected model resource in ZK view.
	 * 
	 * @return selected model resource.
	 */
	public Resource getSelectedResource() {
		return selectedResource;
	}

	/**
	 * Get selected model resource property in ZK view.
	 * 
	 * @return selected model resource property.
	 */
	public ResourceProperty getSelectedResourceProperty() {
		return selectedResourceProperty;
	}

	/**
	 * Set selected model resource property in ZK view.
	 * 
	 * @param selected
	 *            model resource property.
	 */
	public void setSelectedResourceProperty(ResourceProperty property) {
		selectedResourceProperty = property;
	}

	/**
	 * Get selected model credential in ZK view.
	 * 
	 * @return selected model credential.
	 */
	public Credential getSelectedCredential() {
		return selectedCredential;
	}

	/**
	 * Get new credential password.
	 * 
	 * @return password new credential password.
	 */
	public String getNewCredentialPassword() {
		return newCredentialPassword;
	}

	/**
	 * Set new credential password.
	 * 
	 * @param password
	 *            new credential password.
	 */
	public void setNewCredentialPassword(String password) {
		newCredentialPassword = password;
	}

	/**
	 * Get properties for selected resource.
	 * 
	 * @return properties for selected resource.
	 */
	public List<ResourceProperty> getProperties() {
		List<ResourceProperty> properties = new ArrayList<ResourceProperty>();
		if (selectedResource == null)
			return properties;
		if (selectedResource.equals(nullResource))
			return properties;

		List<Property> resourceProps = selectedResource.getProperty();
		for (Property prop : resourceProps) {
			ResourceProperty resourceProperty = new ResourceProperty(prop.getKey(), prop.getValue());
			properties.add(resourceProperty);
		}
		return properties;
	}

	/**
	 * Get new environment as part of creation.
	 * 
	 * @return environment under creation.
	 */
	public Environment getNewEnvironment() {
		return newTempEnvironment;
	}

	/**
	 * Get new resource as part of creation.
	 * 
	 * @return resource under creation.
	 */
	public Resource getNewResource() {
		return newTempResource;
	}

	/**
	 * Get new credential as part of creation.
	 * 
	 * @return credential under creation.
	 */
	public Credential getNewCredential() {
		return newTempCredential;
	}

	/**
	 * Determine which type of state the page is in.
	 * 
	 * @return "NewEnvironment" if a new environment is under creation.
	 *         "NewResource" if a new resource is under creation. "NewCredential" if
	 *         a new credential is under creation. "Environment" if a environment is
	 *         selected an should rendered. "Resource" if a resource is selected an
	 *         should rendered. "Credential" if a credential is selected an should
	 *         rendered. "NothingSelected" if nothing is selected. Textual
	 *         representation of the state.
	 */
	public String getPageState() {
		if (newTempEnvironment != null)
			return ZK_PAGESTATE_NEW_ENVIRONMENT;
		if (newTempResource != null)
			return ZK_PAGESTATE_NEW_RESOURCE;
		if (newTempCredential != null)
			return ZK_PAGESTATE_NEW_CREDENTIAL;
		if (selectedTreeNode == null)
			return ZK_PAGESTATE_NOTHING_SELECTED;

		// get node data
		TreeNodeData treeNodeData = selectedTreeNode.getData();
		if (treeNodeData == null)
			return ZK_PAGESTATE_NOTHING_SELECTED;
		return treeNodeData.getState().toString();
	}

	/**
	 * Returns the value of confirm environment deletion flag. If the flag is true
	 * then the environment deletion modal windows is enabled.
	 * 
	 * @return value of confirm environment deletion flag.
	 */
	public boolean getConfirmEnvironmentDeletion() {
		return stateConfirmEnvironmentDeletion;
	}

	/**
	 * Returns the value of confirm resource deletion flag. If the flag is true then
	 * the resource deletion modal windows is enabled.
	 * 
	 * @return value of confirm resource deletion flag.
	 */
	public boolean getConfirmResourceDeletion() {
		return stateConfirmResourceDeletion;
	}

	/**
	 * Returns the value of confirm resource property deletion flag. If the flag is
	 * true then the resource property deletion modal windows is enabled.
	 * 
	 * @return value of confirm resource property deletion flag.
	 */
	public boolean getConfirmResourcePropertyDeletion() {
		return stateConfirmResourcePropertyDeletion;
	}

	/**
	 * Returns the value of confirm credential deletion flag. If the flag is true
	 * then the credential deletion modal windows is enabled.
	 * 
	 * @return value of confirm credential deletion flag.
	 */
	public boolean getConfirmCredentialDeletion() {
		return stateConfirmCredentialDeletion;
	}

	/**
	 * Event handler for selection of item in tree view.
	 * 
	 * Will populate center panel with selected item details.
	 */
	@Command
	@NotifyChange({ "pageState", "selectedEnvironment", "selectedResource", "selectedCredential", "properties" })
	public void selectTreeItem() {
	}

	/**
	 * Event handler for the global command "createEnvironment". The event is
	 * triggered from the menu controller which posts the global command.
	 * 
	 * Step 1) of creating a the new environment. Will create a new temporary
	 * environment for editing and saving.
	 */
	@GlobalCommand
	@NotifyChange({ "pageState", "newEnvironmentInfo" })
	public void createEnvironment() {
		newTempEnvironment = objectFactory.createEnvironment();
		newTempEnvironment.setId("");
		newTempEnvironment.setDescription("");
	}

	/**
	 * Event handler for save new environment.
	 * 
	 * Step 2) of creating a the new environment. Will save temporary environment in
	 * core component using REST API and update the model.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "newEnvironment" })
	public void saveNewEnvironment() {

		// create environment
		configurationController.createEnvironment(newTempEnvironment.getId(), newTempEnvironment.getDescription());

		// create new model
		createViewModel();

		// clear new environment info
		newTempEnvironment = null;

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.create_environment_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for cancel save new environment.
	 * 
	 * Will nullify reference to temporary environment.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "newEnvironment" })
	public void cancelSaveNewEnvironment() {

		// clear new environment info
		newTempEnvironment = null;
	}

	/**
	 * Event handler for update environment.
	 * 
	 * Will update selected and possibly modified environment in core component
	 * using REST API and update the model.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel" })
	public void updateEnvironment() {

		// resolve original environment ID which might have been overwritten by
		// update
		String originalEnvironmentId = selectedTreeNode.getData().getId();

		// create model
		Configuration configuration = objectFactory.createConfiguration();
		configuration.setEnvironments(objectFactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();
		environments.add(selectedEnvironment);

		// update environment
		configurationController.updateEnvironment(originalEnvironmentId, configuration);

		// create new model
		createViewModel();

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.update_environment_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for the global command "deleteEnvironment". The event is
	 * triggered from the menu controller and the environment configuration panel
	 * which posts the global command.
	 * 
	 * Step 1) of deleting a environment. Will show deletion confirmation dialog.
	 */
	@GlobalCommand
	@NotifyChange({ "confirmEnvironmentDeletion" })
	public void deleteEnvironment() {

		// exit if environment isn't selected
		if (!getPageState().equals(TreeNodeDataValueState.ENVCONFIG_ENVIRONMENT.toString())) {
			String message = webMessageProvider.getMessage("ecp.delete_environment_not_selected_failed");
			Messagebox.show(message);
			return;
		}

		// set confirm deletion state to open modal confirmation window
		stateConfirmEnvironmentDeletion = true;
	}

	/**
	 * Event handler for the command "deleteEnvironmentConfirmed". The event is
	 * triggered from the confirm deletion modal window.
	 * 
	 * Step 2) of deleting a environment. Will close the modal window and delete the
	 * environment.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "confirmEnvironmentDeletion" })
	public void deleteEnvironmentConfirmed() {

		// clear confirm deletion state to close modal window
		stateConfirmEnvironmentDeletion = false;

		// delete environment
		configurationController.deleteEnvironment(selectedEnvironment.getId());

		// clear selected item
		setSelectedItem(null);

		// create new model
		createViewModel();

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.delete_environment_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for the command "deleteEnvironmentCancelled". The event is
	 * triggered from the confirm deletion modal window.
	 * 
	 * Will close the modal window and abort the deletion.
	 */
	@Command
	@NotifyChange({ "confirmEnvironmentDeletion" })
	public void deleteEnvironmentCancelled() {

		// clear confirm deletion state to close modal window
		stateConfirmEnvironmentDeletion = false;
	}

	/**
	 * Event handler for the global command "createResource". The event is triggered
	 * from the menu controller which posts the global command.
	 * 
	 * Step 1) of creating a the new resource. Will create a new temporary resource
	 * for editing and saving.
	 */
	@GlobalCommand
	@NotifyChange({ "pageState", "newResourceInfo" })
	public void createResource() {

		// exit if environment isn't selected
		if (this.selectedEnvironment == null) {
			String message = webMessageProvider.getMessage("ecp.create_resource_environment_not_selected_failed");
			Messagebox.show(message);
			return;
		}

		newTempResource = objectFactory.createResource();
		newTempResource.setId("");
		newTempResource.setCredentialIdRef("");
		newTempResource.setPluginId("");
	}

	/**
	 * Event handler for save new resource.
	 * 
	 * Step 2) of creating a the new resource. + Will save temporary resource in
	 * core component using REST API and update the model.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "newResource" })
	public void saveNewResource() {

		// get environment
		String environment = selectedEnvironment.getId();

		try {

			// create resource
			configurationController.createResource(environment, newTempResource.getId(), newTempResource.getPluginId(),
					newTempResource.getCredentialIdRef());

		} catch (PluginInitializationFailedException e) {

			// show and log error message
			logger.error(StackTraceHelper.getStrackTrace(e));
			Object[] args = { e.getMessage() };
			String message = webMessageProvider.getMessage("ecp.create_resource_plugin_init_failed", args);
			Messagebox.show(message);
			return;
		}

		// clear selected item
		setSelectedItem(null);

		// create new model
		createViewModel();

		// clear new resource info
		newTempResource = null;

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.create_resource_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for cancel save new resource.
	 * 
	 * Will nullify reference to temporary environment.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "newResource" })
	public void cancelSaveNewResource() {

		// clear new environment info
		newTempResource = null;
	}

	/**
	 * Event handler for the global command "deleteResource". The event is triggered
	 * from the menu controller and the environment configuration panel which posts
	 * the global command.
	 * 
	 * Step 1) of deleting a resource. Will show deletion confirmation dialog.
	 */
	@GlobalCommand
	@NotifyChange({ "confirmResourceDeletion" })
	public void deleteResource() {

		// exit if resource isn't selected
		if (!getPageState().equals(TreeNodeDataValueState.ENVCONFIG_RESOURCE.toString())) {
			String message = webMessageProvider.getMessage("ecp.delete_resource_not_selected_failed");
			Messagebox.show(message);
			return;
		}

		// set confirm deletion state to open modal confirmation window
		stateConfirmResourceDeletion = true;
	}

	/**
	 * Event handler for the command "deleteResourceConfirmed". The event is
	 * triggered from the confirm deletion modal window.
	 * 
	 * Step 2) of deleting a resource. Will close the modal window and delete the
	 * resource.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "confirmResourceDeletion" })
	public void deleteResourceConfirmed() {

		// clear confirm deletion state to close modal window
		stateConfirmResourceDeletion = false;

		// resolve environment
		Environment environment = resolveEnvironment();

		// delete resource
		configurationController.deleteResource(environment.getId(), selectedResource.getId());

		// clear selected item
		setSelectedItem(null);

		// create new model
		createViewModel();

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.delete_resource_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for the command "deleteResourceCancelled". The event is
	 * triggered from the confirm deletion modal window.
	 * 
	 * Will close the modal window and abort the deletion.
	 */
	@Command
	@NotifyChange({ "confirmResourceDeletion" })
	public void deleteResourceCancelled() {

		// clear confirm deletion state to close modal window
		stateConfirmResourceDeletion = false;
	}

	/**
	 * Event handler for update resource.
	 * 
	 * Will update selected and possibly modified resource in core component using
	 * REST API and update the model.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel" })
	public void updateResource() {

		// resolve original resource ID which might have been overwritten by
		// update
		String originalResourceId = selectedTreeNode.getData().getId();

		// resolve environment
		Environment resolvedEnvironment = resolveEnvironment();

		// create model with updated resource values
		Configuration configuration = objectFactory.createConfiguration();
		configuration.setEnvironments(objectFactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();
		Environment environment = objectFactory.createEnvironment();
		environments.add(environment);
		environment.setResources(objectFactory.createResources());
		environment.setId(resolvedEnvironment.getId());
		// ignoring other environment properties
		Resource resource = objectFactory.createResource();
		environment.getResources().getResource().add(resource);
		resource.setId(selectedResource.getId());
		resource.setPluginId(selectedResource.getPluginId());
		resource.setCredentialIdRef(selectedResource.getCredentialIdRef());

		// update resource
		configurationController.updateResource(resolvedEnvironment.getId(), originalResourceId, configuration);

		// create new model
		createViewModel();

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.update_resource_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for the global command "createCredential". The event is
	 * triggered from the menu controller which posts the global command.
	 * 
	 * Step 1) of creating a the new credential. Will create a new temporary
	 * credential for editing and saving.
	 */
	@GlobalCommand
	@NotifyChange({ "pageState", "newCredentialInfo" })
	public void createCredential() {

		// exit if environment isn't selected
		if (this.selectedEnvironment == null) {
			String message = webMessageProvider.getMessage("ecp.create_credential_environment_not_selected_failed");
			Messagebox.show(message);
			return;
		}

		newTempCredential = objectFactory.createCredential();
		newTempCredential.setId("");
		newTempCredential.setPassword("");
		newTempCredential.setUser("");
	}

	/**
	 * Event handler for save new credential.
	 * 
	 * Step 2) of creating a the new credential. Will save temporary credential in
	 * core component using REST API and update the model.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "newCredential" })
	public void saveNewCredential() {

		// get environment
		String environment = selectedEnvironment.getId();

		try {

			// create credential
			configurationController.createCredential(environment, newTempCredential.getId(),
					newTempCredential.getUser(), newTempCredential.getPassword());

		} catch (CredentialAlreadyExitsException e) {

			// show and log error message
			logger.error(StackTraceHelper.getStrackTrace(e));
			Object[] args = { e.getMessage() };
			String message = webMessageProvider.getMessage("ecp.create_credential_failed", args);
			Messagebox.show(message);
			return;
		}

		// clear selected item
		setSelectedItem(null);

		// create new model
		createViewModel();

		// clear new credential info
		newTempCredential = null;

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.create_credential_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for cancel save new credential.
	 * 
	 * Will nullify reference to temporary credential.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "newCredential" })
	public void cancelSaveNewCredential() {

		// clear new credential info
		newTempCredential = null;
	}

	/**
	 * Event handler for the global command "deleteCredential". The event is
	 * triggered from the menu controller and the environment configuration panel
	 * which posts the global command.
	 * 
	 * Step 1) of deleting a credential. Will show deletion confirmation dialog.
	 */
	@GlobalCommand
	@NotifyChange({ "confirmCredentialDeletion" })
	public void deleteCredential() {

		// exit if resource isn't selected
		if (!getPageState().equals(TreeNodeDataValueState.ENVCONFIG_CREDENTIAL.toString())) {
			String message = webMessageProvider.getMessage("ecp.delete_credential_not_selected_failed");
			Messagebox.show(message);
			return;
		}

		// set confirm deletion state to open modal confirmation window
		stateConfirmCredentialDeletion = true;
	}

	/**
	 * Event handler for the command "deleteCredentialConfirmed". The event is
	 * triggered from the confirm deletion modal window.
	 * 
	 * Step 2) of deleting a credential. Will close the modal window and delete the
	 * credential.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "confirmCredentialDeletion" })
	public void deleteCredentialConfirmed() {

		// clear confirm deletion state to close modal window
		stateConfirmCredentialDeletion = false;

		// resolve environment
		Environment environment = resolveEnvironment();

		try {

			// delete credential
			configurationController.deleteCredential(environment.getId(), selectedCredential.getId());

		} catch (CredentialNotFoundException e) {

			// show and log error message
			logger.error(StackTraceHelper.getStrackTrace(e));
			Object[] args = { e.getMessage() };
			String message = webMessageProvider.getMessage("ecp.delete_credential_failed", args);
			Messagebox.show(message);
			return;
		}

		// clear selected item
		setSelectedItem(null);

		// create new model
		createViewModel();

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.delete_credential_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for the command "deleteCredentialCancelled". The event is
	 * triggered from the confirm deletion modal window.
	 * 
	 * Will close the modal window and abort the deletion.
	 */
	@Command
	@NotifyChange({ "confirmCredentialDeletion" })
	public void deleteCredentialCancelled() {

		// clear confirm deletion state to close modal window
		stateConfirmCredentialDeletion = false;
	}

	/**
	 * Event handler for update credential.
	 * 
	 * Will update selected and possibly modified credential in the core component
	 * using REST API and update the model.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "selectedCredential" })
	public void updateCredential() {

		// resolve original credential ID which might have been overwritten by
		// update
		String originalCredentialId = selectedTreeNode.getData().getId();

		// resolve environment
		Environment resolvedEnvironment = resolveEnvironment();

		// create model with updated resource values
		Configuration configuration = objectFactory.createConfiguration();
		configuration.setEnvironments(objectFactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();
		Environment environment = objectFactory.createEnvironment();
		environments.add(environment);
		environment.setCredentials(objectFactory.createCredentials());
		environment.setId(resolvedEnvironment.getId());
		Credential credential = objectFactory.createCredential();
		environment.getCredentials().getCredential().add(credential);
		credential.setId(selectedCredential.getId());
		credential.setUser(selectedCredential.getUser());
		credential.setPassword(selectedCredential.getPassword());

		try {
			// update credential
			configurationController.updateCredential(resolvedEnvironment.getId(), originalCredentialId, configuration);

		} catch (CredentialNotFoundException e) {

			// show and log error message
			logger.error(StackTraceHelper.getStrackTrace(e));
			Object[] args = { e.getMessage() };
			String message = webMessageProvider.getMessage("ecp.update_credential_failed", args);
			Messagebox.show(message);
			return;
		}

		// create new model
		createViewModel();

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.update_credential_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for the command "resetCredentialPassword". The event is
	 * triggered from a button on the selected resource which posts the command.
	 * 
	 * Step 1) of resetting the password.. Will open a model window for password
	 * reset.
	 */
	@Command
	public void resetCredentialPassword() {
		Window modalWindow = null;

		try {

			// open modal window
			modalWindow = (Window) Executions.createComponents(RESET_CREDENTIAL_PASSWORD_MODAL_ZUL, NULL_PARENT_WINDOW,
					NULL_COMPONENT_ARGS);
			modalWindow.doModal();

		} catch (Exception e) {

			// show and log error message
			logger.error(StackTraceHelper.getStrackTrace(e));
			Object[] args = { e.getMessage() };
			String message = webMessageProvider.getMessage("ecp.reset_credential_password_failed", args);
			Messagebox.show(message);

			// detach window
			if (modalWindow != null)
				modalWindow.detach();
		}

	}

	/**
	 * Event handler for the global command "resetCredentialPasswordConfirmed". The
	 * event is triggered from the modal window for resetting the credential
	 * password.
	 * 
	 * Step 2) of resetting the password. Will update selected credential with
	 * returned password from the modal window The credential is updated in the core
	 * component using REST API and update the model.
	 * 
	 * @param password
	 *            reset password from modal window.
	 */
	@GlobalCommand
	@NotifyChange({ "pageState", "treeModel" })
	public void resetCredentialPasswordConfirmed(@BindingParam(CREDENTIAL_PASSWORD_ARG) String password) {

		// resolve original credential ID which might have been overwritten by
		// update
		String originalCredentialId = selectedTreeNode.getData().getId();

		// resolve environment
		Environment resolvedEnvironment = resolveEnvironment();

		// create model with updated resource values
		Configuration configuration = objectFactory.createConfiguration();
		configuration.setEnvironments(objectFactory.createEnvironments());
		List<Environment> environments = configuration.getEnvironments().getEnvironment();
		Environment environment = objectFactory.createEnvironment();
		environments.add(environment);
		environment.setCredentials(objectFactory.createCredentials());
		environment.setId(resolvedEnvironment.getId());
		Credential credential = objectFactory.createCredential();
		environment.getCredentials().getCredential().add(credential);
		credential.setId(selectedCredential.getId());
		credential.setUser(selectedCredential.getUser());
		credential.setPassword(password);

		try {
			// update credential
			configurationController.updateCredential(resolvedEnvironment.getId(), originalCredentialId, configuration);

		} catch (CredentialNotFoundException e) {

			// show and log error message
			logger.error(StackTraceHelper.getStrackTrace(e));
			Object[] args = { e.getMessage() };
			String message = webMessageProvider.getMessage("ecp.update_credential_failed", args);
			Messagebox.show(message);
			return;
		}

		// create new model
		createViewModel();

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.update_credential_password_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for the command "createResourceProperty". The event is
	 * triggered from a button on the selected resource which posts the command.
	 * 
	 * Step 1) of creating a new resource property credential. Will open a model
	 * window for property creation.
	 */
	@Command
	public void createResourceProperty() {
		Window modalWindow = null;

		try {

			// add modal arguments
			final HashMap<String, Object> arguments = new HashMap<String, Object>();
			arguments.put("selectedResource", this.selectedResource);

			// open modal window
			modalWindow = (Window) Executions.createComponents(CREATE_RESOURCE_PROPERTY_MODAL_ZUL, NULL_PARENT_WINDOW,
					arguments);
			modalWindow.doModal();

		} catch (Exception e) {

			// show and log error message
			logger.error(StackTraceHelper.getStrackTrace(e));
			Object[] args = { e.getMessage() };
			String message = webMessageProvider.getMessage("ecp.create_resource_property_failed", args);
			Messagebox.show(message);

			// detach window
			if (modalWindow != null)
				modalWindow.detach();
		}

	}

	/**
	 * Event handler for the global command "createResourcePropertyConfirmed". The
	 * event is triggered from the create resource property modal window.
	 *
	 * Step 2) of creating a resource property. Will create resource property on
	 * selected resource with property attributes from the modal window. The
	 * resource is updated in the core component using REST API and update the
	 * model.
	 * 
	 * @param key
	 *            resource property key from modal window.
	 * @param value
	 *            resource property value from modal window.
	 */
	@GlobalCommand
	@NotifyChange({ "pageState", "treeModel" })
	public void createResourcePropertyConfirmed(@BindingParam(RESOURCE_PROPERTY_KEY_ARG) String key,
			@BindingParam(RESOURCE_PROPERTY_VALUE_ARG) String value) {

		// resolve environment
		Environment resolvedEnvironment = resolveEnvironment();

		try {
			// create resource property
			configurationController.createProperty(resolvedEnvironment.getId(), selectedResource.getId(), key, value);

		} catch (Exception e) {

			// show error message
			Object[] args = { e.getMessage() };
			String message = webMessageProvider.getMessage("ecp.create_resource_property_failed", args);
			Messagebox.show(message);
			return;
		}

		// clear selected item
		setSelectedItem(null);

		// create new model
		createViewModel();

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.create_resource_property_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for the command "deleteResourceProperty". The event is
	 * triggered from the environment configuration panel which posts the command.
	 * 
	 * Step 1) of deleting a resource property . Will show deletion confirmation
	 * dialog.
	 */
	@Command
	@NotifyChange({ "confirmResourcePropertyDeletion" })
	public void deleteResourceProperty() {

		if (selectedResourceProperty == null) {
			String message = webMessageProvider.getMessage("ecp.delete_resourceproperty_not_selected_failed");
			Messagebox.show(message);
			return;
		}
		// exit if resource property isn't selected
		if (selectedResourceProperty.equals(nullResourceProperty)) {
			String message = webMessageProvider.getMessage("ecp.delete_resourceproperty_not_selected_failed");
			Messagebox.show(message);
			return;
		}

		// set confirm deletion state to open modal confirmation window
		stateConfirmResourcePropertyDeletion = true;
	}

	/**
	 * Event handler for the command "deleteResourcePropertyConfirmed". The event is
	 * triggered from the confirm deletion modal window.
	 * 
	 * Step 2) of deleting a resource property. Will close the modal window and
	 * delete the resource property.
	 */
	@Command
	@NotifyChange({ "pageState", "treeModel", "confirmResourcePropertyDeletion" })
	public void deleteResourcePropertyConfirmed() {

		// clear confirm deletion state to close modal window
		stateConfirmResourcePropertyDeletion = false;

		// resolve environment
		Environment environment = resolveEnvironment();

		// delete resource property
		configurationController.deleteProperty(environment.getId(), selectedResource.getId(),
				selectedResourceProperty.getKey());

		// clear selected item
		setSelectedItem(null);

		// create new model
		createViewModel();

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.delete_resourceproperty_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);
	}

	/**
	 * Event handler for the command "deleteResourcePropertyCancelled". The event is
	 * triggered from the confirm deletion modal window.
	 * 
	 * Will close the modal window and abort the deletion.
	 */
	@Command
	@NotifyChange({ "confirmResourcePropertyDeletion" })
	public void deleteResourcePropertyCancelled() {

		// clear confirm deletion state to close modal window
		stateConfirmResourcePropertyDeletion = false;
	}

	/**
	 * Event handler for the global command "refreshEnvironmentConfiguration". The
	 * event is triggered from the menu controller and the environment configuration
	 * panel which posts the global command.
	 * 
	 * Will trigger re-initialization of the environment configuration through the
	 * creation of a core component from the web application factory.
	 */
	@GlobalCommand
	@NotifyChange({ "treeModel" })
	public void refreshEnvironmentConfiguration() {

		try {

			// refresh
			configurationController.refresh();

		} catch (CoreException e) {

			// show error message
			Object[] args = { e.getMessage() };
			String message = webMessageProvider.getMessage("ecp.refresh_configration_failed", args);
			Messagebox.show(message);
			return;
		}

		// clear selected item
		setSelectedItem(null);

		// create new model
		createViewModel();

		// post notification
		Clients.showNotification(webMessageProvider.getMessage("ecp.refresh_configration_info"), POST_STYLE, null,
				POST_LOCATION, POST_DURATION);

		// post global command to trigger report update in report panel view
		// model
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, PINEAPPLE_ZK_SCOPE, COMPLETED_OPERATION_GLOBALCOMMAND,
				NULL_GLOBALCOMMAND_ARGS);

		// register refresh activity
		activityRepository.addRefreshConfigurationActivity(sessionState.getAccount());

		// post global command to trigger update of the activity list on the
		// home tab
		BindUtils.postGlobalCommand(PINEAPPLE_ZK_QUEUE, EventQueues.APPLICATION,
				COMPLETED_ACTIVITY_CREATION_GLOBALCOMMAND, NULL_GLOBALCOMMAND_ARGS);

	}

	/**
	 * Create tree node with string as data object.
	 * 
	 * @param value
	 *            string which is inserted as data object in the node.
	 * @param state
	 *            value state which defines the type of node.
	 * 
	 * @return tree node with string as data object.
	 */
	DefaultTreeNode<TreeNodeData> createTreeNode(String value, TreeNodeDataValueState state) {
		TreeNodeData dataObject = new TreeNodeData(value, state);
		return new DefaultTreeNode<TreeNodeData>(dataObject, new ArrayList<DefaultTreeNode<TreeNodeData>>());
	}

	/**
	 * Create tree node with environment as data object.
	 * 
	 * @param environment
	 *            environment which is inserted as data object in the node.
	 * 
	 * @return tree node with environment as data object.
	 */
	DefaultTreeNode<TreeNodeData> createTreeNode(Environment environment) {
		TreeNodeData dataObject = new TreeNodeData(environment);
		return new DefaultTreeNode<TreeNodeData>(dataObject, new ArrayList<DefaultTreeNode<TreeNodeData>>());
	}

	/**
	 * Create leaf tree node with resource as data object.
	 * 
	 * @param resource
	 *            resource which is inserted as data object in the node.
	 * 
	 * @return leaf tree node with resource as data object.
	 */
	DefaultTreeNode<TreeNodeData> createLeafTreeNode(Resource resource) {
		TreeNodeData dataObject = new TreeNodeData(resource);
		return new DefaultTreeNode<TreeNodeData>(dataObject);
	}

	/**
	 * Create leaf tree node with credential as data object.
	 * 
	 * @param credential
	 *            credential which is inserted as data object in the node.
	 * 
	 * @return leaf tree node with credential as data object.
	 */
	DefaultTreeNode<TreeNodeData> createLeafTreeNode(Credential credential) {
		TreeNodeData dataObject = new TreeNodeData(credential);
		return new DefaultTreeNode<TreeNodeData>(dataObject);
	}

	/**
	 * Resolve credential.
	 * 
	 * @return resolved credential.
	 */
	Credential resolveCredential() {

		// declare
		String environmentId = null;
		String credentialId = null;

		try {

			// get environment
			Environment environment = resolveEnvironment();

			// get credential
			environmentId = environment.getId();
			credentialId = selectedResource.getCredentialIdRef();
			Configuration resourceConfiguration = configurationController.getCredential(environmentId, credentialId);

			// Retrieve returned credential
			List<Environment> resultEnvironments = resourceConfiguration.getEnvironments().getEnvironment();
			Environment resultEnvironment = resultEnvironments.get(FIRST_LIST_INDEX);
			List<Credential> resultCredentials = resultEnvironment.getCredentials().getCredential();
			return resultCredentials.get(FIRST_LIST_INDEX);

		} catch (CredentialNotFoundException e) {
			Object[] args = { environmentId, credentialId, StackTraceHelper.getStrackTrace(e) };
			String message = webMessageProvider.getMessage("ecp.resolve_credential_error", args);
			logger.error(message);
			return nullCredential;

		} catch (Exception e) {
			Object[] args = { environmentId, credentialId, StackTraceHelper.getStrackTrace(e) };
			String message = webMessageProvider.getMessage("ecp.resolve_credential_error", args);
			logger.error(message);
			return nullCredential;
		}
	}

	/**
	 * Resolve environment for selected resource.
	 * 
	 * @return resolved environment.
	 */
	Environment resolveEnvironment() {

		// get environment
		TreeNode<TreeNodeData> parentNode = selectedTreeNode.getParent();
		TreeNodeData treeNodeData = parentNode.getData();
		Environment environment = (Environment) treeNodeData.getValue();
		return environment;
	}

}
