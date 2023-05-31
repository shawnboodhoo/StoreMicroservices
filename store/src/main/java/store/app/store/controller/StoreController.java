package store.app.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.app.store.dto.StoreDto;
import store.app.store.model.Stores;
import store.app.store.service.StoreService;

@RestController
@RequestMapping("/store")
public class StoreController {
    @Autowired
    private StoreService StoreService;

    @PostMapping
    public ResponseEntity<?> addStore(@RequestBody StoreDto Store){
        return StoreService.createStore(Store);
    }

    @GetMapping
    public ResponseEntity<?> getAllStores(){
        return StoreService.getAllStores();
    }

    @GetMapping("/uniqueStoreIdentifier/{uniqueStoreIdentifier}")
    public ResponseEntity<?> getStoreByUniqueStoreIdentifier(@PathVariable String uniqueStoreIdentifier){
        return StoreService.getStoreByUniqueStoreIdentifier(uniqueStoreIdentifier);
    }

    @GetMapping("/storeCode/{StoreCode}")
    public ResponseEntity<?> getAllStoresBySameStoreStoreIdentifier(@PathVariable String StoreCode){
        return StoreService.getAllStoresBySameStoreStoreIdentifier(StoreCode);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStoreById(@PathVariable Long id){
        return StoreService.getStoreById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStoreById(@PathVariable Long id){
       return StoreService.deleteStoreById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStore(@PathVariable Long id, @RequestBody StoreDto storeDto){
        return StoreService.updateStore(id, storeDto);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchForStoresInALocation(@RequestParam("store_name") String store_name, @RequestParam("location") String location) {
        return StoreService.searchForStoresInALocation(store_name, location);
    }




    }