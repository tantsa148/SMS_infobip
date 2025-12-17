package sms.client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RestController
@RequestMapping("/api/admin")
public class ControllerLister {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

   @GetMapping("/controllers")
public List<Map<String, String>> listControllers() {
    List<Map<String, String>> list = new ArrayList<>();
    Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();

    
   map.forEach((info, method) -> {
    Map<String, String> m = new HashMap<>();

    // Récupérer les patterns avec la nouvelle API
    String url = "N/A";
    if (info.getPathPatternsCondition() != null) {
        url = info.getPathPatternsCondition().getPatternValues().toString();
    }

    m.put("url", url);
    m.put("controller", method.getBeanType().getSimpleName());
    m.put("method", method.getMethod().getName());
    list.add(m);
});

    return list;
}


}
