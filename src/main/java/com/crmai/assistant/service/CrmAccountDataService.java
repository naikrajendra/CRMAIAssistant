package com.crmai.assistant.service;

import com.crmai.assistant.model.CrmAccount;
import com.crmai.assistant.model.CrmAccountChange;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class CrmAccountDataService {

    private static final List<String> ACCOUNT_NAMES = List.of(
            "Apex Industrial Group", "NorthStar Retail", "Summit Health Partners", "BluePeak Logistics",
            "HarborView Foods", "Crestline Financial", "Velocity Tech Systems", "Pioneer Aero", "Evergreen Energy",
            "Redwood Manufacturing", "Atlas Mobility", "Stonebrook Ventures", "SilverLine Telecom", "Emerald BioWorks",
            "Horizon Legal Group", "Maple Grove Supply", "Cobalt Solutions", "Lattice Commerce", "Pelican Hospitality",
            "Oak & Pine Homes", "Granite Advisory", "Solstice Labs", "Beacon Construction", "Monarch Media",
            "Riverstone Capital",
            "Kite & Key Services", "Nordic Manufacturing", "Harbor Crest Labs", "Verdant Logistics",
            "Cedar Valley Foods",
            "Wavefront Finance", "Urban Nest Homes", "Transit Works", "Peak Valley Energy", "Crown Bridge Partners",
            "Hawthorn Retail", "Lumen Digital", "Prism Clinical", "Bright Harbor Travel", "GrovePoint Systems",
            "Lakeside Ventures", "PrimeLine Distribution", "Summerset Ventures", "Mariner Freight", "SignalWorks Media",
            "DeepWater Pharma", "Westlake Security", "Mason & Moss", "Crosswind Airlines", "Union Square Group",
            "Riverside Industrials", "Crestview Consulting");

    private static final List<String> INDUSTRIES = List.of(
            "Logistics", "Healthcare", "Financial Services", "Technology", "Manufacturing",
            "Retail", "Professional Services", "Energy", "Hospitality", "Real Estate");

    private static final List<String> REGIONS = List.of("North America", "EMEA", "APAC", "LATAM");

    public List<CrmAccount> loadAccounts() {
        List<CrmAccount> accounts = new ArrayList<>();
        for (int index = 0; index < ACCOUNT_NAMES.size(); index++) {
            String name = ACCOUNT_NAMES.get(index);
            accounts.add(new CrmAccount(
                    "ACCT-" + (1000 + index),
                    name,
                    INDUSTRIES.get(index % INDUSTRIES.size()),
                    REGIONS.get(index % REGIONS.size()),
                    "Account Owner " + (index + 1),
                    60 + (index % 40),
                    buildSampleChanges(index, name)));
        }
        return accounts;
    }

    private List<CrmAccountChange> buildSampleChanges(int index, String name) {
        List<CrmAccountChange> changes = new ArrayList<>();
        changes.add(new CrmAccountChange("LEAD", "New inbound lead from expansion opportunity for " + name,
                8500 + index * 70, "Medium"));
        changes.add(new CrmAccountChange("QUOTE", "Enterprise quote delivered for refreshed procurement package",
                160000 + index * 1200, "High"));
        changes.add(new CrmAccountChange("OPPORTUNITY",
                "Renewal opportunity in pipeline with executive sponsor alignment", 420000 + index * 2500, "High"));
        changes.add(new CrmAccountChange("ORDER", "Follow-up order request confirmed for fulfillment scheduling",
                90000 + index * 500, "Medium"));

        if (index % 2 == 0) {
            changes.add(new CrmAccountChange("CASE", "Customer support case escalated for implementation coordination",
                    20000 + index * 110, "High"));
        }

        return changes;
    }
}
