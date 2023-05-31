package store.app.store.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import store.app.store.dto.StoreDto;
import store.app.store.exceptions.ErrorStatusCodeMessage;
import store.app.store.model.Stores;
import store.app.store.repository.StoreRepository;
import store.app.store.response.CodeMessage;
import store.app.store.response.StatusCodeMessageData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ModelMapper modelMapper;


    public ResponseEntity<?> createStore(StoreDto storeDto) {
           try {
               if (storeRepository.existsByStoreName(storeDto.getStoreName()) && !storeRepository.existsByStoreName(storeDto.getStoreName())) {
                   ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "A store with the same store identifier is already assigned to a different store name.");
                   return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
               } else if (!storeRepository.existsBystoreIdentifier(storeDto.getSameStoreStoreIdentifier()) && storeRepository.existsByStoreName(storeDto.getStoreName())) {
                   ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "Store with store name already exists");
                   return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
               } else if (storeRepository.uniqueStoreIdentifierExists(storeDto.getUniqueStoreIdentifier())) {
                   ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "Store UniqueStoreIdentifier already exists");
                   return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
               }

               Stores store = modelMapper.map(storeDto, Stores.class);

               // convert dto to entity

               Stores storeSave = storeRepository.save(store);

               StoreDto postResponse = modelMapper.map(store, StoreDto.class);

               // convert entity to dto
               StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Store created", postResponse);

               return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
           } catch (DataIntegrityViolationException e) {
                   ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Can't post null values:" + " " + e.getMessage());
                   return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
               ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
           // create a store
           // another store can't be created with the same store identifier and a different name
           // Vice versa another store can't be created with a name that already exists with a different store identifier
            // Keeps same stores organized with store names
    }


    public ResponseEntity<?> getAllStores(){
        List<Stores> storeList = (List<Stores>) storeRepository.findAll();
        if (storeList.isEmpty()){
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "No stores available");
      return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        List<StoreDto> storeDtos = storeList.stream().map(stores -> modelMapper.map(stores, StoreDto.class))
                .collect(Collectors.toList());
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Got all stores", storeDtos);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
        }

    public ResponseEntity<?> getAllStoresBySameStoreStoreIdentifier (String sameStoreStoreIdentifier){
        List<Stores> storesList = storeRepository.getAllStoresBysameStoreStoreIdentifier(sameStoreStoreIdentifier);
        if (storesList.isEmpty()){
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Store identifier not found");
       return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
      List<StoreDto> storeDtos = storesList.stream().map(stores -> modelMapper.map(stores, StoreDto.class))
                .collect(Collectors.toList());
            StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Store identifier found", storeDtos);
            return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
        }


    public ResponseEntity<?> getStoreById (Long id) {
            Optional<Stores> store = storeRepository.findById(id);
            if (store.isEmpty()){
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "store ID not found");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
            }
            StoreDto postResponse = modelMapper.map(store.get(), StoreDto.class);
            StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Store found", postResponse);
            return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
        }

    public ResponseEntity<?> getStoreByUniqueStoreIdentifier (String uniqueStoreIdentifier) {
        Optional<Stores> store = storeRepository.getStoreByuniqueStoreIdentifier(uniqueStoreIdentifier);
        if (store.isEmpty()){
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "store uniqueStoreIdentifier not found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        StoreDto postResponse = modelMapper.map(store.get(), StoreDto.class);
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Store found", postResponse);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }



    public ResponseEntity<?> deleteStoreById(Long id) {
        Optional<Stores> storesOptional = storeRepository.findById(id);
        if (storesOptional.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Store ID not found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        } else {
            storeRepository.deleteById(id);
            CodeMessage codeMessage = new CodeMessage(HttpStatus.OK.value(), "Store deleted");
            return new ResponseEntity<>(codeMessage, HttpStatus.OK);
        }
    }


    public ResponseEntity<?> updateStore(Long id, StoreDto storeDto) {
        Optional<Stores> optionalStore = storeRepository.findById(id);
        if (optionalStore.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Store ID not found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }if (storeRepository.existsBystoreIdentifier(storeDto.getSameStoreStoreIdentifier()) && !storeRepository.existsByStoreName(storeDto.getStoreName())) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "A store with the same store identifier is already assigned to a different store name.");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
        } else if (!storeRepository.existsBystoreIdentifier(storeDto.getSameStoreStoreIdentifier()) && storeRepository.existsByStoreName(storeDto.getStoreName())) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "A store with the same name already exists.");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
        } else if (storeRepository.uniqueStoreIdentifierExists(storeDto.getUniqueStoreIdentifier())) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "Store UniqueStoreIdentifier already exists");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
        }
        Stores existingStore = optionalStore.get();

        modelMapper.map(storeDto, existingStore);

        existingStore.setId(id);

        Stores updatedStore = storeRepository.save(existingStore);

        StoreDto updatedStoreDto = modelMapper.map(updatedStore, StoreDto.class);

        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Store updated", updatedStoreDto);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }

    public ResponseEntity<?> searchForStoresInALocation(String store_name, String location) {
        List<Stores> stores = storeRepository.findByStoreNameContainingAndLocation(store_name, location);
        if (stores.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Store not found in this location");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        List<StoreDto> storeDtoList = stores.stream().map(store -> modelMapper.map(store, StoreDto.class))
                .collect(Collectors.toList());
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Stores found in this location", storeDtoList);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }
}