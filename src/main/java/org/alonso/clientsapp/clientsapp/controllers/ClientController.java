package org.alonso.clientsapp.clientsapp.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.alonso.clientsapp.clientsapp.models.entity.Client;
import org.alonso.clientsapp.clientsapp.models.entity.Region;
import org.alonso.clientsapp.clientsapp.models.services.ClientService;
import org.alonso.clientsapp.clientsapp.models.services.UploadFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private UploadFileService uploadService;

    private final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @GetMapping
    public List<Client> index() {
        return clientService.findAll();
    }

    @GetMapping("/page/{page}")
    public Page<Client> index(@PathVariable Integer page) {
        return clientService.findAll(PageRequest.of(page, 8));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Client> optionalClient = null;
        Map<String, Object> response = new HashMap<>();

        try {
            optionalClient = clientService.findById(id);
        } catch (DataAccessException e) {
            response.put("msg", "Error en la conexión a la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return ResponseEntity.internalServerError().body(response);
        }

        if (optionalClient.isPresent()) {
            response.put("client", optionalClient.orElseThrow());
            return ResponseEntity.ok().body(response);
        }

        response.put("msg", "No existe un cliente con el id " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> store(@Valid @RequestBody Client client, BindingResult result) {
        Client newClient = null;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            newClient = clientService.save(client);
        } catch (DataAccessException e) {
            response.put("msg", "Error en la conexión a la base de datos");
            return ResponseEntity.internalServerError().body(response);
        }

        response.put("client", newClient);
        response.put("msg", "Cliente creado con exito");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> update(@Valid @RequestBody Client client, BindingResult result, @PathVariable Long id) {
        Optional<Client> optionalClient = null;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            optionalClient = clientService.findById(id);

            if (optionalClient.isPresent()) {
                Client currentClient = optionalClient.orElseThrow();

                currentClient.setName(client.getName());
                currentClient.setLastname(client.getLastname());
                currentClient.setEmail(client.getEmail());
                currentClient.setPhone(client.getPhone());
                currentClient.setBirthdate(client.getBirthdate());
                currentClient.setRegion(client.getRegion());

                response.put("client", clientService.save(currentClient));
                response.put("msg", "Cliente actualizado con exito");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
        } catch (DataAccessException e) {
            response.put("msg", "Error en la conexión a la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return ResponseEntity.internalServerError().body(response);
        }

        response.put("msg", "No existe un cliente con el id " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> destroy(@PathVariable Long id) {
        Optional<Client> optionalClient = null;
        Map<String, Object> response = new HashMap<>();

        try {
            optionalClient = clientService.findById(id);

            if (optionalClient.isPresent()) {
                Client client = optionalClient.orElseThrow();

                uploadService.delete(client.getImage(), "clients");
                clientService.delete(id);
                response.put("msg", "Cliente eliminado con exito");

                return ResponseEntity.ok().body(response);
            }

        } catch (DataAccessException e) {
            response.put("msg", "Error en la conexión a la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return ResponseEntity.internalServerError().body(response);
        }

        response.put("msg", "No existe un cliente con el id " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
        Optional<Client> optionalClient = null;
        Map<String, Object> response = new HashMap<>();

        try {
            optionalClient = clientService.findById(id);

            if (!optionalClient.isPresent()) {
                response.put("msg", "No existe un cliente con el id " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (file.isEmpty()) {
                response.put("error", "El archivo es obligatorio");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Client client = optionalClient.orElseThrow();
            String filename = null;

            try {
                filename = uploadService.copy(file, "clients");
            } catch (IOException e) {
                response.put("msg", "No se pudo subir el archivo");
                return ResponseEntity.internalServerError().body(response);
            }

            uploadService.delete(client.getImage(), "clients");

            client.setImage(filename);
            client = clientService.save(client);

            response.put("msg", "Imagen subida con exito");
            response.put("client", client);

            return ResponseEntity.created(null).body(response);
        } catch (DataAccessException e) {
            response.put("msg", "No se pudo subir el archivo");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/upload/{filename}")
    public ResponseEntity<Resource> showImage(@PathVariable String filename) {
        Resource resource = null;

        try {
            resource = uploadService.load(filename, "clients");
        } catch (MalformedURLException e) {
            logger.info("Error: No se pudo cargar la imagen");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @GetMapping("/regions")
    public List<Region> regions() {
        return clientService.findRegions();
    }
}
