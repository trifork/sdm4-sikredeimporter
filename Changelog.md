
## sikredeimporter 4.3
* Fixed NSPSUPPORT-154 Grundet ændringer i input formatet kan SIkraftDatoYderGl indeholde udelukkende nuller i stedet
  for en korrekt dato, dette omgås nu ved at indsætte epoch dato i stedet for at fejle.

## sikredeimporter 4.4
*  Løser NSPSUPPORT-167 fejlende import.
   Det er løst ved at tillade flere samtidige behandlingsrelationer mellem patienter og læger.

## sikredeimporter 4.5
*  SDM-19 Sikrede register mangler index på CPR-nummer - løst med en simpel database migrering.

## sikredeimporter 4.6
*  Opdateret SDM4 depencencies
*  SDM-5 SLA-log fra SDM4-importere følger ikke standarden
*  Tilføjet kopi register view, så kopi register service maps nu bliver oprettet automatisk
*  Tilføjet nyt index der er gør krs kopiering hurtigere.
