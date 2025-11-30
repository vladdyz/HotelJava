package ca.senecapolytechnic.application.apd545project.repositories;

import ca.senecapolytechnic.application.apd545project.models.ServiceAddon;

import java.util.List;

public interface AddonRepository {
    void save(ServiceAddon addon);
    ServiceAddon findById(Long id);
    ServiceAddon findByName(String name);
    List<ServiceAddon> findAll();
    void update(ServiceAddon addon);
    void delete(Long id);
}
