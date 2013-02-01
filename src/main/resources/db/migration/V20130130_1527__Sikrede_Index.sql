ALTER TABLE AssignedDoctor
ADD INDEX cprDocAssigned (patientCpr ASC, doctorOrganisationIdentifier ASC, assignedTo ASC) ;