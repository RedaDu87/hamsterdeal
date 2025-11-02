package com.example.annonces.mapper;

import com.example.annonces.domain.Ad;
import com.example.annonces.dto.AdRequest;

public final class AdMapper {

    private AdMapper(){}

    public static Ad toEntity(AdRequest req, String ownerId){
        Ad ad = new Ad();
        ad.setId(req.getId());
        ad.setTitle(req.getTitle());
        ad.setDescription(req.getDescription());
        ad.setPrice(req.getPrice());
        ad.setCategory(req.getCategory());
        ad.setCanton(req.getCanton());
        ad.setOwnerId(ownerId);

        ad.setCondition(req.getCondition());
        ad.setBrand(req.getBrand());
        ad.setColor(req.getColor());
        ad.setCity(req.getCity());

        // Car
        if (req.getCar()!=null){
            Ad.CarAttrs a = new Ad.CarAttrs();
            var d = req.getCar();
            a.setMake(d.getMake());
            a.setModel(d.getModel());
            a.setYear(d.getYear());
            a.setMileage(d.getMileage());
            a.setFuel(d.getFuel());
            a.setTransmission(d.getTransmission());
            a.setBodyType(d.getBodyType());
            a.setDoors(d.getDoors());
            a.setSeats(d.getSeats());
            a.setPowerHp(d.getPowerHp());
            a.setDrivetrain(d.getDrivetrain());
            a.setVin(d.getVin());
            ad.setCar(a);
        }

        // Electronics
        if (req.getElectronics()!=null){
            Ad.ElectronicsAttrs a = new Ad.ElectronicsAttrs();
            var d = req.getElectronics();
            a.setType(d.getType());
            a.setModel(d.getModel());
            a.setStorageGb(d.getStorageGb());
            a.setRamGb(d.getRamGb());
            a.setCpu(d.getCpu());
            a.setScreen(d.getScreen());
            a.setOs(d.getOs());
            ad.setElectronics(a);
        }

        // Home
        if (req.getHome()!=null){
            Ad.HomeAttrs a = new Ad.HomeAttrs();
            var d = req.getHome();
            a.setSubcategory(d.getSubcategory());
            a.setMaterial(d.getMaterial());
            a.setDimensions(d.getDimensions());
            a.setAssembled(d.getAssembled());
            ad.setHome(a);
        }

        // Fashion
        if (req.getFashion()!=null){
            Ad.FashionAttrs a = new Ad.FashionAttrs();
            var d = req.getFashion();
            a.setGender(d.getGender());
            a.setSize(d.getSize());
            a.setMaterial(d.getMaterial());
            a.setStyle(d.getStyle());
            ad.setFashion(a);
        }

        // Sports
        if (req.getSports()!=null){
            Ad.SportsAttrs a = new Ad.SportsAttrs();
            var d = req.getSports();
            a.setSport(d.getSport());
            a.setEquipment(d.getEquipment());
            a.setSize(d.getSize());
            ad.setSports(a);
        }

        // Toys
        if (req.getToys()!=null){
            Ad.ToysAttrs a = new Ad.ToysAttrs();
            var d = req.getToys();
            a.setAgeRange(d.getAgeRange());
            a.setMaterial(d.getMaterial());
            a.setEducational(d.getEducational());
            ad.setToys(a);
        }

        // Books
        if (req.getBooks()!=null){
            Ad.BooksAttrs a = new Ad.BooksAttrs();
            var d = req.getBooks();
            a.setAuthor(d.getAuthor());
            a.setLanguage(d.getLanguage());
            a.setFormat(d.getFormat());
            a.setIsbn(d.getIsbn());
            a.setGenre(d.getGenre());
            a.setYear(d.getYear());
            ad.setBooks(a);
        }

        // Music
        if (req.getMusic()!=null){
            Ad.MusicAttrs a = new Ad.MusicAttrs();
            var d = req.getMusic();
            a.setMedia(d.getMedia());
            a.setArtist(d.getArtist());
            a.setAlbum(d.getAlbum());
            a.setGenre(d.getGenre());
            a.setYear(d.getYear());
            a.setInstrument(d.getInstrument());
            ad.setMusic(a);
        }

        // Tools
        if (req.getTools()!=null){
            Ad.ToolsAttrs a = new Ad.ToolsAttrs();
            var d = req.getTools();
            a.setType(d.getType());
            a.setPower(d.getPower());
            a.setVoltage(d.getVoltage());
            a.setProGrade(d.getProGrade());
            ad.setTools(a);
        }

        // Garden
        if (req.getGarden()!=null){
            Ad.GardenAttrs a = new Ad.GardenAttrs();
            var d = req.getGarden();
            a.setType(d.getType());
            a.setSeason(d.getSeason());
            a.setMotorized(d.getMotorized());
            ad.setGarden(a);
        }

        // Pets
        if (req.getPets()!=null){
            Ad.PetsAttrs a = new Ad.PetsAttrs();
            var d = req.getPets();
            a.setSpecies(d.getSpecies());
            a.setBreed(d.getBreed());
            a.setAgeMonths(d.getAgeMonths());
            a.setSex(d.getSex());
            a.setVaccinated(d.getVaccinated());
            a.setMicrochipped(d.getMicrochipped());
            ad.setPets(a);
        }

        // Jobs
        if (req.getJobs()!=null){
            Ad.JobsAttrs a = new Ad.JobsAttrs();
            var d = req.getJobs();
            a.setRole(d.getRole());
            a.setCompany(d.getCompany());
            a.setContractType(d.getContractType());
            a.setExperience(d.getExperience());
            a.setRemote(d.getRemote());
            a.setSalaryMin(d.getSalaryMin());
            a.setSalaryMax(d.getSalaryMax());
            ad.setJobs(a);
        }

        // Services
        if (req.getServices()!=null){
            Ad.ServicesAttrs a = new Ad.ServicesAttrs();
            var d = req.getServices();
            a.setServiceType(d.getServiceType());
            a.setAvailability(d.getAvailability());
            a.setProfessional(d.getProfessional());
            a.setHourlyRate(d.getHourlyRate());
            ad.setServices(a);
        }

        // RealEstate
        if (req.getRealestate()!=null){
            Ad.RealEstateAttrs a = new Ad.RealEstateAttrs();
            var d = req.getRealestate();
            a.setPropertyType(d.getPropertyType());
            a.setArea(d.getArea());
            a.setRooms(d.getRooms());
            a.setBathrooms(d.getBathrooms());
            a.setFurnished(d.getFurnished());
            a.setParking(d.getParking());
            a.setFloor(d.getFloor());
            a.setYearBuilt(d.getYearBuilt());
            ad.setRealestate(a);
        }

        // Collectibles
        if (req.getCollectibles()!=null){
            Ad.CollectiblesAttrs a = new Ad.CollectiblesAttrs();
            var d = req.getCollectibles();
            a.setCollectibleType(d.getCollectibleType());
            a.setEra(d.getEra());
            a.setCertified(d.getCertified());
            ad.setCollectibles(a);
        }

        return ad;
    }

    public static AdRequest toRequest(Ad ad) {
        AdRequest req = new AdRequest();

        // Champs communs
        req.setId(ad.getId());
        req.setTitle(ad.getTitle());
        req.setDescription(ad.getDescription());
        req.setPrice(ad.getPrice());
        req.setCategory(ad.getCategory());
        req.setCanton(ad.getCanton());
        req.setCondition(ad.getCondition());
        req.setBrand(ad.getBrand());
        req.setColor(ad.getColor());
        req.setCity(ad.getCity());

        // Car
        if (ad.getCar() != null) {
            var a = ad.getCar();
            var d = new AdRequest.CarDTO();
            d.setMake(a.getMake());
            d.setModel(a.getModel());
            d.setYear(a.getYear());
            d.setMileage(a.getMileage());
            d.setFuel(a.getFuel());
            d.setTransmission(a.getTransmission());
            d.setBodyType(a.getBodyType());
            d.setDoors(a.getDoors());
            d.setSeats(a.getSeats());
            d.setPowerHp(a.getPowerHp());
            d.setDrivetrain(a.getDrivetrain());
            d.setVin(a.getVin());
            req.setCar(d);
        }

        // Electronics
        if (ad.getElectronics()!=null){
            var a = ad.getElectronics();
            var d = new AdRequest.ElectronicsDTO();
            d.setType(a.getType());
            d.setModel(a.getModel());
            d.setStorageGb(a.getStorageGb());
            d.setRamGb(a.getRamGb());
            d.setCpu(a.getCpu());
            d.setScreen(a.getScreen());
            d.setOs(a.getOs());
            req.setElectronics(d);
        }

        // Home
        if (ad.getHome()!=null){
            var a = ad.getHome();
            var d = new AdRequest.HomeDTO();
            d.setSubcategory(a.getSubcategory());
            d.setMaterial(a.getMaterial());
            d.setDimensions(a.getDimensions());
            d.setAssembled(a.getAssembled());
            req.setHome(d);
        }

        // Fashion
        if (ad.getFashion()!=null){
            var a = ad.getFashion();
            var d = new AdRequest.FashionDTO();
            d.setGender(a.getGender());
            d.setSize(a.getSize());
            d.setMaterial(a.getMaterial());
            d.setStyle(a.getStyle());
            req.setFashion(d);
        }

        // Sports
        if (ad.getSports()!=null){
            var a = ad.getSports();
            var d = new AdRequest.SportsDTO();
            d.setSport(a.getSport());
            d.setEquipment(a.getEquipment());
            d.setSize(a.getSize());
            req.setSports(d);
        }

        // Toys
        if (ad.getToys()!=null){
            var a = ad.getToys();
            var d = new AdRequest.ToysDTO();
            d.setAgeRange(a.getAgeRange());
            d.setMaterial(a.getMaterial());
            d.setEducational(a.getEducational());
            req.setToys(d);
        }

        // Books
        if (ad.getBooks()!=null){
            var a = ad.getBooks();
            var d = new AdRequest.BooksDTO();
            d.setAuthor(a.getAuthor());
            d.setLanguage(a.getLanguage());
            d.setFormat(a.getFormat());
            d.setIsbn(a.getIsbn());
            d.setGenre(a.getGenre());
            d.setYear(a.getYear());
            req.setBooks(d);
        }

        // Music
        if (ad.getMusic()!=null){
            var a = ad.getMusic();
            var d = new AdRequest.MusicDTO();
            d.setMedia(a.getMedia());
            d.setArtist(a.getArtist());
            d.setAlbum(a.getAlbum());
            d.setGenre(a.getGenre());
            d.setYear(a.getYear());
            d.setInstrument(a.getInstrument());
            req.setMusic(d);
        }

        // Tools
        if (ad.getTools()!=null){
            var a = ad.getTools();
            var d = new AdRequest.ToolsDTO();
            d.setType(a.getType());
            d.setPower(a.getPower());
            d.setVoltage(a.getVoltage());
            d.setProGrade(a.getProGrade());
            req.setTools(d);
        }

        // Garden
        if (ad.getGarden()!=null){
            var a = ad.getGarden();
            var d = new AdRequest.GardenDTO();
            d.setType(a.getType());
            d.setSeason(a.getSeason());
            d.setMotorized(a.getMotorized());
            req.setGarden(d);
        }

        // Pets
        if (ad.getPets()!=null){
            var a = ad.getPets();
            var d = new AdRequest.PetsDTO();
            d.setSpecies(a.getSpecies());
            d.setBreed(a.getBreed());
            d.setAgeMonths(a.getAgeMonths());
            d.setSex(a.getSex());
            d.setVaccinated(a.getVaccinated());
            d.setMicrochipped(a.getMicrochipped());
            req.setPets(d);
        }

        // Jobs
        if (ad.getJobs()!=null){
            var a = ad.getJobs();
            var d = new AdRequest.JobsDTO();
            d.setRole(a.getRole());
            d.setCompany(a.getCompany());
            d.setContractType(a.getContractType());
            d.setExperience(a.getExperience());
            d.setRemote(a.getRemote());
            d.setSalaryMin(a.getSalaryMin());
            d.setSalaryMax(a.getSalaryMax());
            req.setJobs(d);
        }

        // Services
        if (ad.getServices()!=null){
            var a = ad.getServices();
            var d = new AdRequest.ServicesDTO();
            d.setServiceType(a.getServiceType());
            d.setAvailability(a.getAvailability());
            d.setProfessional(a.getProfessional());
            d.setHourlyRate(a.getHourlyRate());
            req.setServices(d);
        }

        // RealEstate
        if (ad.getRealestate()!=null){
            var a = ad.getRealestate();
            var d = new AdRequest.RealEstateDTO();
            d.setPropertyType(a.getPropertyType());
            d.setArea(a.getArea());
            d.setRooms(a.getRooms());
            d.setBathrooms(a.getBathrooms());
            d.setFurnished(a.getFurnished());
            d.setParking(a.getParking());
            d.setFloor(a.getFloor());
            d.setYearBuilt(a.getYearBuilt());
            req.setRealestate(d);
        }

        // Collectibles
        if (ad.getCollectibles()!=null){
            var a = ad.getCollectibles();
            var d = new AdRequest.CollectiblesDTO();
            d.setCollectibleType(a.getCollectibleType());
            d.setEra(a.getEra());
            d.setCertified(a.getCertified());
            req.setCollectibles(d);
        }

        return req;
    }
}
