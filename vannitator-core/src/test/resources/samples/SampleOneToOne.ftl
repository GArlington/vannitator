/*
 * Vannitator generated from: ${vtype.qualifiedName}
 * http://vanitator.soqqo.org
 * 
 * This is the EntityProxy of {@link ${vtype.qualifiedName}} for the GWT RequestFactory
 */
package ${vtype.packageName.asNew};

import com.google.gwt.requestfactory.shared.EntityProxy;
import com.google.gwt.requestfactory.shared.ProxyFor;

// import ${vtype.qualifiedName};

<#list vtype.dependantTypes as imp>
import ${imp};
</#list>


/*

   ${vtype.simpleClassName} --> ${vtype.simpleClassName.asNew}
   ${vtype.packageName} --> ${vtype.packageName.asNew}
   ${vtype.qualifiedName} --> ${vtype.qualifiedName.asNew}
*/

@ProxyFor(${vtype.simpleClassName}.class)
public interface ${vtype.simpleClassName.asNew} extends EntityProxy {

    /*
      fields:
     <#list vtype.fields as field>
        ${field};     
     </#list>
    */
       
    <#list vtype.methods as method>
       ${method.asNew};
    </#list>

}