
-- -----------------------------------------------------
-- Someone has to create the SKRS tables first time
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SKRSViewMapping` (
  `idSKRSViewMapping` BIGINT(15) NOT NULL AUTO_INCREMENT ,
  `register` VARCHAR(255) NOT NULL ,
  `datatype` VARCHAR(255) NOT NULL ,
  `version` INT NOT NULL ,
  `tableName` VARCHAR(255) NOT NULL ,
  `createdDate` TIMESTAMP NOT NULL ,
  PRIMARY KEY (`idSKRSViewMapping`) ,
  INDEX `idx` (`register` ASC, `datatype` ASC, `version` ASC) ,
  UNIQUE INDEX `unique` (`register` ASC, `datatype` ASC, `version` ASC) )
  ENGINE = InnoDB;
CREATE  TABLE IF NOT EXISTS `SKRSColumns` (
  `idSKRSColumns` BIGINT(15) NOT NULL AUTO_INCREMENT ,
  `viewMap` BIGINT(15) NOT NULL ,
  `isPID` TINYINT NOT NULL ,
  `tableColumnName` VARCHAR(255) NOT NULL ,
  `feedColumnName` VARCHAR(255) NULL ,
  `feedPosition` INT NOT NULL ,
  `dataType` INT NOT NULL ,
  `maxLength` INT NULL ,
  PRIMARY KEY (`idSKRSColumns`) ,
  INDEX `viewMap_idx` (`viewMap` ASC) ,
  UNIQUE INDEX `viewColumn` (`tableColumnName` ASC, `viewMap` ASC) ,
  CONSTRAINT `viewMap`
  FOREIGN KEY (`viewMap` )
  REFERENCES `SKRSViewMapping` (`idSKRSViewMapping` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;

-- ---------------------------------------------------------------------------------------------------------------------
-- Sikrede
-- ---------------------------------------------------------------------------------------------------------------------

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('sikrede', 'sikrede', 1, 'Sikrede', NOW());
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 1, 'PID',                                       NULL, 0,  -5, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'CPRnr',                                        'CPRnr', 2,  12, 10),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SYdernr',                                    'SYdernr', 3,  12, 6),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SIkraftDatoYder',                    'SIkraftDatoYder', 4,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SRegDatoYder',                          'SRegDatoYder', 5,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSikrGrpKode',                          'SSikrGrpKode', 6,  12, 1),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SIkraftDatoGrp',                      'SIkraftDatoGrp', 7,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SRegDatoGrp',                            'SRegDatoGrp', 8,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSikrKomKode',                          'SSikrKomKode', 9,  12, 3),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SIkraftDatoKomKode',              'SIkraftDatoKomKode',10,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SYdernrGl',                                'SYdernrGl',11,  12, 6),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SIkraftDatoYderGl',                'SIkraftDatoYderGl',12,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SRegDatoYderGl',                      'SRegDatoYderGl',13,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSikrGrpKodeGl',                      'SSikrGrpKodeGl',14,  12, 1),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SIkraftDatoGrpGl',                  'SIkraftDatoGrpGl',15,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SRegDatoGrpGl',                        'SRegDatoGrpGl',16,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SYdernrFrem',                            'SYdernrFrem',17,  12, 6),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SIkraftDatoYderFrem',            'SIkraftDatoYderFrem',18,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SRegDatoYderFrem',                  'SRegDatoYderFrem',19,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSikrGrpKodeFrem',                  'SSikrGrpKodeFrem',20,  12, 1),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SIkraftDatoGrpFrem',              'SIkraftDatoGrpFrem',21,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SRegDatoGrpFrem',                    'SRegDatoGrpFrem',22,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SKon',                                          'SKon',23,  12, 1),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SAlder',                                      'SAlder',24,  12, 3),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SFolgerskabsPerson',              'SFolgerskabsPerson',25,  12, 10),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SStatus',                                    'SStatus',26,  12, 2),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SBevisDato',                              'SBevisDato',27,  12, 8),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'PNavn',                                        'PNavn',28,  12, 34),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SBSStatsborgerskabKode',      'SBSStatsborgerskabKode',29,  12, 2),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SBSStatsborgerskab',              'SBSStatsborgerskab',30,  12, 47),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKAdrLinie1',                          'SSKAdrLinie1',31,  12, 40),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKAdrLinie2',                          'SSKAdrLinie2',32,  12, 40),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKBopelsLand',                        'SSKBopelsLand',33,  12, 40),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKBopelsLAndKode',                'SSKBopelsLAndKode',34,  12, 2),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKEmailAdr',                            'SSKEmailAdr',35,  12, 50),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKFamilieRelation',              'SSKFamilieRelation',36,  12, 10),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKFodselsdato',                      'SSKFodselsdato',37,  12, 10),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKGyldigFra',                          'SSKGyldigFra',38,  12, 10),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKGyldigTil',                          'SSKGyldigTil',39,  12, 10),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKMobilNr',                              'SSKMobilNr',40,  12, 20),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSKPostNrBy',                            'SSKPostNrBy',41,  12, 40),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSLForsikringsinstans',        'SSLForsikringsinstans',42,  12, 21),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSLForsikringsinstansKode','SSLForsikringsinstansKode',43,  12, 10),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSLForsikringsnr',                  'SSLForsikringsnr',44,  12, 15),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSLGyldigFra',                          'SSLGyldigFra',45,  12, 10),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSLGyldigTil',                          'SSLGyldigTil',56,  12, 10),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSLSocSikretLand',                  'SSLSocSikretLand',57,  12, 47),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'SSLSocSikretLandKode',          'SSLSocSikretLandKode',58,  12, 2),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'ModifiedDate',                                    NULL, 0,  93, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'ValidFrom',                                'validFrom',59,  93, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sikrede' AND datatype='sikrede' AND version=1), 0, 'ValidTo',                                    'validTo',60,  93, NULL);