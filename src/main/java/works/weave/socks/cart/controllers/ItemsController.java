package works.weave.socks.cart.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import works.weave.socks.cart.cart.CartDAO;
import works.weave.socks.cart.cart.CartResource;
import works.weave.socks.cart.entities.Item;
import works.weave.socks.cart.item.FoundItem;
import works.weave.socks.cart.item.ItemDAO;
import works.weave.socks.cart.item.ItemResource;

import java.util.List;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

@Api(tags = "items 操作")
@RestController
@RequestMapping(value = "/carts/{customerId:.*}/items")
public class ItemsController {
    private final Logger LOG = getLogger(getClass());

    @Autowired
    private ItemDAO itemDAO;
    @Autowired
    private CartsController cartsController;
    @Autowired
    private CartDAO cartDAO;

    @ApiOperation(value = "get item's infos from itemId and customerId",
            extensions = @Extension(properties = {@ExtensionProperty(name = "x-forward-compatible-marker", value = "0")})
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{itemId:.*}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Item get(@PathVariable String customerId, @PathVariable String itemId) {
        return new FoundItem(() -> getItems(customerId), () -> new Item(itemId)).get();
    }

    @ApiOperation(value = "get item's infos from itemId",
            extensions = @Extension(properties = {@ExtensionProperty(name = "x-forward-compatible-marker", value = "0")})
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> getItems(@PathVariable String customerId) {
        return cartsController.get(customerId).contents();
    }

    @ApiOperation(value = "save item's infos from customerId and itemId",
            extensions = @Extension(properties = {@ExtensionProperty(name = "x-forward-compatible-marker", value = "1")})
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Item addToCart(@PathVariable String customerId, @RequestBody Item item) {
        // If the item does not exist in the cart, create new one in the repository.
        FoundItem foundItem = new FoundItem(() -> cartsController.get(customerId).contents(), () -> item);
        if (!foundItem.hasItem()) {
            Supplier<Item> newItem = new ItemResource(itemDAO, () -> item).create();
            LOG.debug("Did not find item. Creating item for user: " + customerId + ", " + newItem.get());
            new CartResource(cartDAO, customerId).contents().get().add(newItem).run();
            return item;
        } else {
            Item newItem = new Item(foundItem.get(), foundItem.get().quantity() + 1);
            LOG.debug("Found item in cart. Incrementing for user: " + customerId + ", " + newItem);
            updateItem(customerId, newItem);
            return newItem;
        }
    }

    @ApiOperation(value = "delete item's infos from customerId and itemId",
            extensions = @Extension(properties = {@ExtensionProperty(name = "x-forward-compatible-marker", value = "0")})
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(value = "/{itemId:.*}")
    public String removeItem(@PathVariable String customerId, @PathVariable String itemId) {
        FoundItem foundItem = new FoundItem(() -> getItems(customerId), () -> new Item(itemId));
        Item item = foundItem.get();

        LOG.debug("Removing item from cart: " + item);
        new CartResource(cartDAO, customerId).contents().get().delete(() -> item).run();

        LOG.debug("Removing item from repository: " + item);
        new ItemResource(itemDAO, () -> item).destroy().run();
        return "success";
    }

    @ApiOperation(value = "update item's infos from customerId and a instance of item",
            extensions = @Extension(properties = {@ExtensionProperty(name = "x-forward-compatible-marker", value = "0")})
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateItem(@PathVariable String customerId, @RequestBody Item item) {
        // Merge old and new items
        ItemResource itemResource = new ItemResource(itemDAO, () -> get(customerId, item.itemId()));
        LOG.debug("Merging item in cart for user: " + customerId + ", " + item);
        itemResource.merge(item).run();
    }
}
