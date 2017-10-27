package com.sample.assetshare.content.properties.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import java.util.Calendar;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import com.adobe.aem.commons.assetshare.content.properties.AbstractComputedProperty;
import com.adobe.aem.commons.assetshare.content.properties.ComputedProperty;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;

@Component(service = ComputedProperty.class)
@Designate(ocd = AssetStatusImpl.Cfg.class)
public class AssetStatusImpl extends AbstractComputedProperty<String> {
    
    //Human readable label
    public static final String LABEL = "Asset Status";
    
    //Name to identify the property in the ValueMap i.e asset.properties['assetStatus']
    public static final String NAME = "assetStatus";
    private Cfg cfg;
    
    private static final int DEFAULT_DAYS = -7;
    private static final String DEFAULT_NEW_LABEL = "new";
    private static final String DEFAULT_UPDATED_LABEL = "updated";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getLabel() {
        return cfg.label();
    }

    @Override
    public String[] getTypes() {
       return cfg.types();
    }

    @Override
    public String get(Asset asset, SlingHttpServletRequest request) {
        
        final ValueMap assetProperties = getAssetProperties(asset);
        Calendar assetCreated = assetProperties.get(JcrConstants.JCR_CREATED, Calendar.class);
        Calendar assetModified = assetProperties.get(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_LASTMODIFIED, Calendar.class);
        
        //Get a calendar to compare to
        Calendar weekOld = getCompareCalendar(cfg.days());
       
        if(assetCreated.after(weekOld)) {
            //if asset created < one week ago
            return cfg.newLabel();
        } else if (assetModified.after(weekOld)) {
            //if asset modified < one week ago
            return cfg.updatedLabel();
        }
        return null;
    }

    @Activate
    protected void activate(Cfg cfg) {
        this.cfg = cfg;
    }
    
    @ObjectClassDefinition(name = "Sample Asset Share - Computed Property - Asset Status")
    public @interface Cfg {
        @AttributeDefinition(
                name = "Label",
                description = "Human read-able label."
        )
        String label() default LABEL;

        @AttributeDefinition(
                name = "Types",
                description = "Defines the type of data this exposes. This classification allows for intelligent exposure of Computed Properties in DataSources, etc."
        )
        String[] types() default {Types.METADATA};
        
        @AttributeDefinition(
                name = "Days",
                description = "Defines the number of days in which an asset is considered 'New' or 'Updated'. Expected to be a negative number."
        )
        int days() default DEFAULT_DAYS;
        
        @AttributeDefinition(
                name = "New Label",
                description = "Text to display as 'New' status."
        )
        String newLabel() default DEFAULT_NEW_LABEL;
        
        @AttributeDefinition(
                name = "Updated Label",
                description = "Text to display as 'Updated' status."
        )
        String updatedLabel() default DEFAULT_UPDATED_LABEL;
    }
    
    /***
     * 
     * @return a Calendar object to compare asset dates to
     */
    private Calendar getCompareCalendar(int daysOld) {
        Calendar compareCal = Calendar.getInstance();
        // reset hour, minutes, seconds and millis
        compareCal.set(Calendar.HOUR_OF_DAY, 0);
        compareCal.set(Calendar.MINUTE, 0);
        compareCal.set(Calendar.SECOND, 0);
        compareCal.set(Calendar.MILLISECOND, 0);
        compareCal.add(Calendar.DAY_OF_MONTH, daysOld);
        
        return compareCal;
    }

}
