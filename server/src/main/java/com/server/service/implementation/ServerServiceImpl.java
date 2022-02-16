package com.server.service.implementation;

import com.server.enumeration.Status;
import com.server.model.Server;
import com.server.repo.ServerRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.server.service.ServerService;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.PageRequest.*;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService {

    private final ServerRepo serverRepo;

    @Override
    public Server create(Server server) {
        log.info("Saving new server: {}", server.getName());
        server.setImageURL(setServerImageURL());
        return serverRepo.save(server);
    }

    @Override
    public Server ping(String ipAddress) throws IOException {
        log.info("Pinging server IP: {}", ipAddress);
        Server server = serverRepo.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000) ? Status.SERVER_UP : Status.SERVER_DOWN);
        serverRepo.save(server);

        return server;
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("Fetching all Servers");
        return serverRepo.findAll(of(0,limit)).toList();
    }

    @Override
    public Server get(Long id) {
        log.info("Fetching Server by id: {}", id);
        return serverRepo.findById(id).get();
    }

    @Override
    public Server update(Server server, long id) {

        if (server.getId() == null){
            Server serverHasNoID = serverRepo.findByIpAddress(server.getIpAddress());
            server.setId(serverHasNoID.getId());
        }

        log.info("Updating server: {}", server.getName());
        return serverRepo.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("deleting server: {}", id);
         serverRepo.deleteById(id);
        return TRUE;
    }

    private String setServerImageURL() {
        String[] imageNames = { "server1.png", "server2.png", "server3.png", "server4.png" };
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/server/image" + imageNames[new Random().nextInt(4)]).toUriString();
    }
}
