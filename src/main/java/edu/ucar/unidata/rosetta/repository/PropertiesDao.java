package edu.ucar.unidata.rosetta.repository;

/**
 * The data access object representing a rosetta property.
 *
 * @author oxelson@ucar.edu
 */
public interface PropertiesDao {
    /**
     * Looks up and retrieves the persisted uploads directory.
     *
     * @return The persisted uploads directory.
     */
    public String lookupUploadDirectory();

    /**
     * Looks up and retrieves the persisted downloads directory.
     *
     * @return The persisted downloads directory.
     */
    public String lookupDownloadDirectory();
}