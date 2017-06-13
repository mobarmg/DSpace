/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.discovery;

import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.dspace.content.DSpaceObject;
import org.dspace.content.IMetadataValue;
import org.dspace.content.Item;
import org.dspace.content.IMetadataValue;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.discovery.configuration.DiscoverySearchFilter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: kevin
 * Date: 03/10/13
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
public class SolrServiceSpellIndexingPlugin implements SolrServiceIndexPlugin {

    @Autowired(required = true)
    protected ItemService itemService;

    @Override
    public void additionalIndex(Context context, DSpaceObject dso, SolrInputDocument document, Map<String, List<DiscoverySearchFilter>> searchFilters) {
        if(dso instanceof Item){
            Item item = (Item) dso;
            List<IMetadataValue> dcValues = itemService.getMetadata(item, Item.ANY, Item.ANY, Item.ANY, Item.ANY);
            List<String> toIgnoreMetadataFields = SearchUtils.getIgnoredMetadataFields(item.getType());
            for (IMetadataValue dcValue : dcValues) {
                if(!toIgnoreMetadataFields.contains(dcValue.getMetadataField().toString('.'))){
                    document.addField("a_spell", dcValue.getValue());
                }
            }
        }
    }
}
