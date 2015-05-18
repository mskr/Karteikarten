
/**
 * Dokumentation der Attribute
 *  removePlugins -> Entfernt plugins. Bei 'toolbar' wird die ganze Toolbar entfernt
 *  resize_enabled -> Fenster ist durch den Nutzer vergrößerbar. Bei False verschwindet auch der untere Graue Rand.
 *  config.enterMode = CKEDITOR.ENTER_BR; -> Erstellt bei "Enter" anstatt eines <p> nur ein <br>. 
 *  										Dadurch kleinere Zeilenabstände, aber nicht recommended laut ckeditor-doku.
 *  toolbarGroups -> Gibt an welche toolbars sichtbar sind und wie sie grupiert sind.
 */
var ckEditorKommentarConfig = {
		height: "100px",
		removePlugins: 'elementspath',
		// TODO Formeleditor erlaubt, um formeln im kommentare zu erstellen
		toolbarGroups: [
		        		{ name: 'insert' }
		        		],
		removeButtons: 'Image,Flash,Table,HorizontalRule',
		resize_enabled : false,
};

var ckEditorKommentarAntwortConfig = {
		height: "60px",
		removePlugins: 'toolbar,elementspath',
		resize_enabled : false,
};

var ckEditorNotizConfig = {
		removePlugins: 'toolbar,elementspath',
//		resize_maxHeight: "500px",
//		resize_minHeight: "40px",
//		height: "100%",
		autoGrow_onStartup : false,
		resize_enabled : false,
}


var ckEditorVnErstellenConfig = {
		toolbarGroups: [
		{ name: 'forms' },
		{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
		{ name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ] },
		{ name: 'insert' },
		{ name: 'styles' },
		{ name: 'colors' },
		{ name: 'tools' },
		{ name: 'others' },
		],
		removeButtons: 'Styles',
		removePlugins: 'elementspath'
};