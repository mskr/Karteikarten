
/**
 * Dokumentation der Attribute
 *  removePlugins -> Entfernt plugins. Bei 'toolbar' wird die ganze Toolbar entfernt
 *  resize_enabled -> Fenster ist durch den Nutzer vergrößerbar. Bei False verschwindet auch der untere Graue Rand.
 *  config.enterMode = CKEDITOR.ENTER_BR; -> Erstellt bei "Enter" anstatt eines <p> nur ein <br>. 
 *  										Dadurch kleinere Zeilenabstände, aber nicht recommended laut ckeditor-doku.
 *  toolbarGroups -> Gibt an welche toolbars sichtbar sind und wie sie grupiert sind.
 */

CKEDITOR.env.isCompatible = true;
CKEDITOR.disableAutoInline = true;

var ckEditorKommentarConfig = {
		height: "100px",
		toolbarGroups: [
		                { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
		        		{ name: 'insert' }
		        		],
		resize_enabled : false,
        removeButtons: 
            "Image," +
    		"Flash," +
    		"Table," +
    		"HorizontalRule," +
            "Maximize," +
            "Format," +
            "Strike," +
            "Subscript," +
            "Superscript," +
            "Blockquote",
        removePlugins: 'elementspath, autogrow, maximize, toolbarswitch'
};

var ckEditorNotizConfig = {
        resize_enabled : false,
		autoGrow_minHeight: 50,
        autoGrow_onStartup: true,
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
		removeButtons: 
		    "Image," +
			"Flash," +
			"Table," +
			"HorizontalRule," +
			"Maximize," +
			"Format," +
			"Strike," +
			"Subscript," +
			"Superscript," +
			"Blockquote,",
		removePlugins: 'elementspath, maximize, toolbarswitch'
};


var ckEditorVnErstellenConfig = {
        resize_enabled : false,
        autoGrow_minHeight: 50,
        autoGrow_onStartup: true,
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
		removePlugins: 'elementspath, toolbarswitch, format'
};