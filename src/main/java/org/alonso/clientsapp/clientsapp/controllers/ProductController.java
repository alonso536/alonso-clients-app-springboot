package org.alonso.clientsapp.clientsapp.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.alonso.clientsapp.clientsapp.models.entity.Product;
import org.alonso.clientsapp.clientsapp.models.services.ProductService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UploadFileService uploadService;

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping
    public List<Product> index() {
        return productService.findAll();
    }

    @GetMapping("/page/{page}")
    public Page<Product> index(@PathVariable Integer page) {
        return productService.findAll(PageRequest.of(page, 9));
    }

    @GetMapping("/{term}")
    public List<Product> search(@PathVariable String term) {
        return productService.findByName(term);
    }

    @PostMapping
    public ResponseEntity<?> store(@Valid @RequestBody Product product, BindingResult result) {
        Product newProduct = null;
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
            newProduct = productService.save(product);
        } catch (DataAccessException e) {
            response.put("msg", "Error en la conexión a la base de datos");
            return ResponseEntity.internalServerError().body(response);
        }

        response.put("product", newProduct);
        response.put("msg", "Producto creado con exito");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Product product, BindingResult result, @PathVariable Long id) {
        Optional<Product> optionalProduct = null;
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
            optionalProduct = productService.findById(id);

            if (optionalProduct.isPresent()) {
                Product currentProduct = optionalProduct.orElseThrow();

                if (!currentProduct.getStatus()) {
                    response.put("msg", "No existe un producto con el id " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                currentProduct.setName(product.getName());
                currentProduct.setDescription(product.getDescription());
                currentProduct.setPrice(product.getPrice());
                currentProduct.setStock(product.getStock());

                response.put("product", productService.save(currentProduct));
                response.put("msg", "Producte actualizado con exito");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
        } catch (DataAccessException e) {
            response.put("msg", "Error en la conexión a la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return ResponseEntity.internalServerError().body(response);
        }

        response.put("msg", "No existe un producto con el id " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> destroy(@PathVariable Long id) {
        Optional<Product> optionalProduct = null;
        Map<String, Object> response = new HashMap<>();

        try {
            optionalProduct = productService.findById(id);
        } catch (DataAccessException e) {
            response.put("msg", "No se pudo eliminar el producto");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return ResponseEntity.internalServerError().body(response);
        }

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.orElseThrow();

            if (!product.getStatus()) {
                response.put("msg", "No existe un producto con el id " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            product.setStatus(false);

            productService.save(product);
            response.put("msg", "Producto eliminado con exito");

            return ResponseEntity.ok().body(response);
        }

        response.put("msg", "No existe un producto con el id " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
        Optional<Product> optionalProduct = null;
        Map<String, Object> response = new HashMap<>();

        try {
            optionalProduct = productService.findById(id);

            if (!optionalProduct.isPresent()) {
                response.put("msg", "No existe un producto con el id " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (file.isEmpty()) {
                response.put("error", "El archivo es obligatorio");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Product product = optionalProduct.orElseThrow();
            String filename = null;

            try {
                filename = uploadService.copy(file, "products");
            } catch (IOException e) {
                response.put("msg", "No se pudo subir el archivo");
                return ResponseEntity.internalServerError().body(response);
            }

            uploadService.delete(product.getImage(), "products");

            product.setImage(filename);
            product = productService.save(product);

            response.put("msg", "Imagen subida con exito");
            response.put("product", product);

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
            resource = uploadService.load(filename, "products");
        } catch (MalformedURLException e) {
            logger.info("Error: No se pudo cargar la imagen");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
