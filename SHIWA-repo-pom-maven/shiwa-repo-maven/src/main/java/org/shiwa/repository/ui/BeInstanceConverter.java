/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shiwa.repository.ui;

import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.shiwa.repository.toolkit.wfengine.BeInstance;

/**
 *
 * @author edward
 */
public class BeInstanceConverter implements Converter{

    @EJB
    BackingBean backingBean;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        backingBean = (BackingBean) context.getApplication().evaluateExpressionGet(context, "#{backingBean}", BackingBean.class);
        return backingBean.getBeInstanceByName(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((BeInstance) value).getName();
    }

}
