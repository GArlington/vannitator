/*
 * Vannitator generated from: ${annotatedClass}
 * http://vanitator.soqqo.org
 * 
 * This is the GWT RequestFactory
 */
package ${packageName};

import com.google.gwt.requestfactory.shared.RequestFactory;

public interface ${conf.newClassName()} extends RequestFactory {

    <#list vtypes as vtype>
       ${vtype.simpleClassName.asNew}Request ${vtype.simpleClassName.asNew?uncap_first}Request();
    </#list>

}