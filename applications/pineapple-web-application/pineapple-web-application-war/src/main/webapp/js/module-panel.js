
/**
 * Declare ACE editor.
 */
var editor;

/**
 * Client side event handler for onSelect event for list box.
 */
function onSelectInListbox() {
	// zk.log("onSelectInListbox:");
};

/**
 * Create ACE editor with document.
 * 
 *  The method is invoked from the view model when a model is selected.
 */
function createAceEditorWithDoc(value) {
	var divComponent = zk.Widget.$("$aceEditor").uuid;
	editor = ace.edit(divComponent);
	editor.setTheme("ace/theme/chrome");
	editor.getSession().setMode("ace/mode/xml");
	editor.setValue(value);
	editor.navigateFileStart();
}

/**
 * Close ACE editor.
 * 
 *  The method is invoked from the view model when a module is closed.
 */
function closeAceEditor() {
	if(editor == null) return;
	editor.destroy();
	var oldDiv = editor.container;
	if(oldDiv == null) return;	
	var newDiv = oldDiv.cloneNode(false);
	oldDiv.parentNode.replaceChild(newDiv, oldDiv);	
}

/**
 * Request document for saving. 
 * 
 * Gets document from editor and sends document to server for 
 * further processing, e.g. saving. 
 * 
 * The document is sent in the event requestedDocumentForSave which
 * is handled by the composer com.alpha.pineapple.web.zk.controller.ModulePanel.
 */
function requestDocumentToSave() {
	if (editor == null) return;
	
	// get document
	var document = editor.getValue();
	
	// get target widget
	var target = zk.Widget.$('$modulePanelWindow');
	
	// send document to server
	zAu.send(new zk.Event(target, 'onRequestedDocumentToSave', {'':{'editor-document':document}}, {toServer:true}));		
}
