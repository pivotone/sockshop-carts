package works.weave.socks.cart.controllers;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import works.weave.socks.cart.cart.CartDAO;
import works.weave.socks.cart.cart.CartResource;
import works.weave.socks.cart.entities.Cart;
import works.weave.socks.cart.entities.Result;
import works.weave.socks.cart.utils.ResultUtil;


@Api(tags = "cart 操作")
@RestController
@RequestMapping(path = "/carts")
public class CartsController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CartDAO cartDAO;

    @ApiOperation(value = "get infos from customerId",
        extensions = @Extension(properties = {@ExtensionProperty(name = "x-forward-compatible-marker", value = "0")})
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result get(@PathVariable String customerId) {
        return ResultUtil.success(new CartResource(cartDAO, customerId).value().get());
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiOperation(value = "delete infos from customerId",
            extensions = @Extension(properties = {@ExtensionProperty(name = "x-forward-compatible-marker", value = "1")})
    )
    @DeleteMapping (value = "/{customerId}")
    public Result delete(@PathVariable String customerId) {
        new CartResource(cartDAO, customerId).destroy().run();
        return ResultUtil.success();
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiOperation(value = "merge infos from customerId",
            extensions = @Extension(properties = {@ExtensionProperty(name = "x-forward-compatible-marker", value = "1")})
    )
    @GetMapping(value = "/{customerId}/merge")
    public Result mergeCarts(@PathVariable String customerId, @RequestParam(value = "sessionId") String sessionId) {
        logger.debug("Merge carts request received for ids: " + customerId + " and " + sessionId);
        CartResource sessionCart = new CartResource(cartDAO, sessionId);
        CartResource customerCart = new CartResource(cartDAO, customerId);
        customerCart.merge(sessionCart.value().get()).run();
        delete(sessionId);
        return ResultUtil.success();
    }
}
