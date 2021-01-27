package de.majaf.voci.control.service;

import de.mschoettle.entity.dto.FileDTO;
import de.mschoettle.entity.dto.FolderDTO;

import java.util.List;

/**
 * Service interface for managing, using and manipulating data received by the Dropsi-partner-project from Mathias Schoettle
 */
public interface IDropsiService {

    /**
     * Filters all {@link FileDTO}s in a Dropsi-rootFolder and renames all of these files to have the complete path as a name
     * @param rootFolder Dropsi-rootFolder, maybe received from calling Dropsi-API
     * @return list of all {@link FileDTO}s recursively found in rootFolder
     */
    List<FileDTO> getFilesFromRootFolder(FolderDTO rootFolder);
}
