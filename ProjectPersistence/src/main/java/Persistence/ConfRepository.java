package Persistence;

import Model.Conf;

public interface ConfRepository extends Repository<Integer, Conf>{
    Conf saveConf(Conf conf);
}
