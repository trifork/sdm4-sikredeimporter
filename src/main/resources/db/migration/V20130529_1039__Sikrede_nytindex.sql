
-- Speeder krs kopiering op
ALTER TABLE `Sikrede`
ADD INDEX `ModifiedPID` (`ModifiedDate` ASC, `PID` ASC) ;
