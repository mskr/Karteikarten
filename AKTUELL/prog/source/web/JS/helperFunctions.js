/**
 * Enthält nützliche Hilfsfunktionen, die uns die Arbeit erleichtern.
 */

/**
 * Füllt eine gegebenee selection mit optionen
 * @param select
 * @param optArray Array mit den Einträgen als String
 * @param slectedOptName Option die gewählt sein soll
 * @param clearFirst Wenn true, dann werden alle alten Optionen erst entfernt
 */
function fillSelectWithOptions(select, optArray, selectedOptName, clearFirst) 
{
	if(clearFirst == true)
		$(select).find('option').remove();

    for(var i in optArray) 
    {
        $(select).append("<option value = '" + optArray[i] +"'>"+optArray[i]+"</option>");
    }

    $(select).find("option[value='"+ selectedOptName +"']").prop('selected', true);
}