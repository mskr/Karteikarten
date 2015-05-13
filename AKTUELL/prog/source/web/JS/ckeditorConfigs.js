
/**
 * Dokumentation der Attribute
 *  removePlugins -> Entfernt plugins. Bei 'toolbar' wird die ganze Toolbar entfernt
 *  resize_enabled -> Fenster ist durch den Nutzer vergrößerbar. Bei False verschwindet auch der untere Graue Rand.
 *  config.enterMode = CKEDITOR.ENTER_BR; -> Erstellt bei "Enter" anstatt eines <p> nur ein <br>. 
 *  										Dadurch kleinere Zeilenabstände, aber nicht recommended laut ckeditor-doku.
 *  
 */
var ckEditorKommentarConfig = {
		height: "100px",
		removePlugins: 'toolbar'
};

var ckEditorKommentarAntwortConfig = {
		height: "60px",
		removePlugins: 'toolbar',
		resize_enabled : false
};

var ckEditorNotizConfig = {
		removePlugins: 'toolbar',
		resize_maxHeight: "1000px",
		resize_minHeight: "50px",
		height: "100%",
//		autoGrow_onStartup : false,
		resize_enabled : false
}