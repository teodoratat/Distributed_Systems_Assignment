package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.EnergySentDTO;
import ro.tuc.ds2020.entities.EnergySent;

public class EnergySentBuilder {
    private EnergySentBuilder() {
    }

    public static EnergySentDTO toEnergySentDTO(EnergySent energySent) {
        return new EnergySentDTO(energySent.getId(), energySent.getDate(), energySent.getValue(), energySent.getDevice());
    }

    public static EnergySent toEntity(EnergySentDTO energySentDTO) {
        return new EnergySent( energySentDTO.getId(), energySentDTO.getDate(), energySentDTO.getValue(), energySentDTO.getDevice());
    }
    public static EnergySentDTO toEnergySentDTO1(EnergySent energySent) {
        return new EnergySentDTO(energySent.getId(), energySent.getDate(), energySent.getValue(), energySent.getDeviceId());
    }

//    public static EnergySent toEntity1(EnergySentDTO energySentDTO) {
//        return new EnergySent( energySentDTO.getId(), energySentDTO.getDate(), energySentDTO.getValue(), energySentDTO.getDeviceId());
//    }

}
