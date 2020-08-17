package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Client;
import cn.p00q.u2ps.mapper.ClientMapper;
import cn.p00q.u2ps.service.ClientService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @program: u2ps
 * @description: 实现类
 * @author: DanBai
 * @create: 2020-08-04 22:01
 **/
@Service
public class ClientServiceImpl implements ClientService {
    private  ClientMapper clientMapper;

    public ClientServiceImpl(ClientMapper clientMapper) {
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

    @Override
    public Integer onlineClientCount() {
        //按条件查询
        Client client = new Client();
        client.setOnLine(true);
        return clientMapper.selectCount(client);
    }

    @Override
    public List<Client> getClientByUsername(String username) {
        Client client = new Client();
        client.setUsername(username);
        return clientMapper.select(client);
    }

    @Override
    public Result updateById(Client c) {
        if(c.getSecretKey()!=null){
            if(c.getSecretKey().equals(Client.AutoGenerate)){
                String key;
                do{
                    key=UUID.randomUUID().toString().replace("-", "");
                }while (getClientByKey(key)!=null);
                c.setSecretKey(key);
            }else {
                if(getClientByKey(c.getSecretKey())!=null){
                    return Result.err("key不唯一");
                }
            }
        }
        return clientMapper.updateByPrimaryKeySelective(c)>0?Result.success("更新成功"):Result.err("更新失败");
    }

    @Override
    public boolean delete(Integer id) {
        return clientMapper.delete(getClientById(id))>0;
    }

    @Override
    public Result create(Client client) {
        client.setOnLine(false);
        client.setCreationTime(new Date());
        if(client.getSecretKey()!=null){
            if(client.getSecretKey().equals(Client.AutoGenerate)){
                String key;
                do{
                    key=UUID.randomUUID().toString().replace("-", "");
                }while (getClientByKey(key)!=null);
                client.setSecretKey(key);
            }else {
                if(getClientByKey(client.getSecretKey())!=null){
                    return Result.err("key不唯一");
                }
            }
        }
        return clientMapper.insert(client)>0?Result.success("创建成功"):Result.err("创建失败");
    }

    @Override
    public List<Client> getUserClientNewTunnel(String username) {
        Example commentExample = new Example(Client.class);
        commentExample.selectProperties("id","remark");
        commentExample.createCriteria().andEqualTo("username",username);
        return clientMapper.selectByExample(commentExample);
    }
}
