package works.weave.socks.cart.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import works.weave.socks.cart.entities.HealthCheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "活跃检测 操作")
@RestController
public class HealthCheckController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, path = "/health")
    @ApiOperation(value = "check health of this service",
            extensions = @Extension(properties = {@ExtensionProperty(name = "x-forward-compatible-marker", value = "0")})
    )
    public
    @ResponseBody
    Map<String, List<HealthCheck>> getHealth() {
       Map<String, List<HealthCheck>> map = new HashMap<String, List<HealthCheck>>();
       List<HealthCheck> healthChecks = new ArrayList<HealthCheck>();
       Date dateNow = Calendar.getInstance().getTime();

       HealthCheck app = new HealthCheck("carts", "OK", dateNow);
       HealthCheck database = new HealthCheck("carts-db", "OK", dateNow);

       try {
          mongoTemplate.executeCommand("{ buildInfo: 1 }");
       } catch (Exception e) {
          database.setStatus("err");
       }

       healthChecks.add(app);
       healthChecks.add(database);

       map.put("health", healthChecks);
       return map;
    }
}
