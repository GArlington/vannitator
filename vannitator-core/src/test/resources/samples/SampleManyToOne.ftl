/*
 * Vannitator generated from: ${vtype.simpleClassName}
 * http://vanitator.soqqo.org
 * 
 * This file is a SAMPLE Annotation FreeMarker Template.
 */
package ${vtype.packageName.asNew};

import com.google.gwt.requestfactory.shared.RequestFactory;

public interface ${vtype.simpleClassName} extends RequestFactory {

    <#list vtypes as vtype>
       ${vtype.simpleClassName.asNew}Request ${vtype.simpleClassName.asNew?uncap_first}Request();
    </#list>

}