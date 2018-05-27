package com.rengu.machinereadingcomprehension.Service;

import com.rengu.machinereadingcomprehension.Entity.CrewEntity;
import com.rengu.machinereadingcomprehension.Repository.CrewRepository;
import com.rengu.machinereadingcomprehension.Utils.MachineReadingComprehensionApplicationMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CrewService {

    private final CrewRepository crewRepository;

    @Autowired
    public CrewService(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    public CrewEntity saveCrew(CrewEntity crewArgs) {
        if (crewArgs == null) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.CREW_PARAM_NOT_FOUND);
        }
        if (StringUtils.isEmpty(crewArgs.getName())) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.CREW_NAME_PARAM_NOT_FOUND);
        }
        CrewEntity crewEntity = new CrewEntity();
        BeanUtils.copyProperties(crewArgs, crewEntity, "id", "createTime");
        return crewRepository.save(crewEntity);
    }

    public CrewEntity deleteCrew(String crewId) {
        if (StringUtils.isEmpty(crewId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.CREW_ID_PARAM_NOT_FOUND);
        }
        CrewEntity crewEntity = getCrewById(crewId);
        crewRepository.deleteById(crewId);
        return crewEntity;
    }

    public CrewEntity getCrewById(String crewId) {
        if (StringUtils.isEmpty(crewId)) {
            throw new RuntimeException(MachineReadingComprehensionApplicationMessage.CREW_ID_PARAM_NOT_FOUND);
        }
        return crewRepository.findById(crewId).get();
    }
}
