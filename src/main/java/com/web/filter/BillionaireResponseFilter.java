package com.web.filter;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.model.Billionaire;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

@Slf4j
public class BillionaireResponseFilter {

    private static final String BillionaireFilter = "billionaireFilter";

    @JsonFilter(BillionaireFilter)
    class DataMixIn {}

    public static String filterFields(final List<Billionaire> billionaires,
                                              final String fields) throws JsonProcessingException{
        final StringTokenizer st = new StringTokenizer(fields, ",");

        final Set<String> filterProperties = new HashSet<>();
        while(st.hasMoreTokens()){
            filterProperties.add(st.nextToken());
        }

        final ObjectMapper objectMapper = new ObjectMapper().addMixIn(Billionaire.class, DataMixIn.class);
        final FilterProvider filterProvider = new SimpleFilterProvider().addFilter(BillionaireFilter,
                SimpleBeanPropertyFilter.filterOutAllExcept(filterProperties));

        try {
            return objectMapper.setFilterProvider(filterProvider).writeValueAsString(billionaires);
        } catch (final JsonProcessingException e){
            log.error(e.getMessage());
            throw e;
        }
    }
}
