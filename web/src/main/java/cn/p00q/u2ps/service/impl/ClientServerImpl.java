package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.entity.Client;
import cn.p00q.u2ps.mapper.ClientMapper;
import cn.p00q.u2ps.service.ClientServer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: u2ps
 * @description: 实现类
 * @author: DanBai
 * @create: 2020-08-04 22:01
 **/
@Service
public class ClientServerImpl implements ClientServer {
    private final ClientMapper clientMapper;

    public ClientServerImpl(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    @Override
    public Client getClientByKey(String key) {
        Client client = new Client();
        client.setSecretKey(key);
        return clientMapper.selectOne(client);
    }

    @Override
    public void setOnline(Integer clientId, boolean online) {
        Client clientById = getClientById(clientId);
        if(clientById!=null){
            clientById.setOnLine(online);
            clientMapper.updateByPrimaryKey(clientById);
        }
    }

    @Override
    public Client getClientById(Integer id) {
        Client client = new Client();
        client.setId(id);
        return clientMapper.selectOne(client);
    }

    @Override
    public void setOnlineAndIp(Integer clientId, boolean online, String Ip) {
        Client clientById = getClientById(clientId);
        clientById.setOnLine(online);
        clientById.setClientIp(Ip);
        clientMapper.updateByPrimaryKey(clientById);
    }

    @Override
    public List<Client> getClientsByIp(String ip) {
        Client client = new Client();
        client.setClientIp(ip);
        return clientMapper.select(client);
    }
}
