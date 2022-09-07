/*
Copyright 2022 Jose Morales joseluis.delacruz@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.jos.dem.vetlog.service.impl;

import com.jos.dem.vetlog.command.Command;
import com.jos.dem.vetlog.command.MessageCommand;
import com.jos.dem.vetlog.command.TelephoneCommand;
import com.jos.dem.vetlog.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jos.dem.vetlog.model.Pet;
import com.jos.dem.vetlog.model.User;


import com.jos.dem.vetlog.enums.PetStatus;
import com.jos.dem.vetlog.service.PetService;
import com.jos.dem.vetlog.service.RestService;
import com.jos.dem.vetlog.service.TelephoneService;
import com.jos.dem.vetlog.repository.PetRepository;
import com.jos.dem.vetlog.repository.UserRepository;

import java.io.IOException;


@Service
public class TelephoneServiceImpl implements TelephoneService {

  @Autowired
  private PetService petService;
  @Autowired
  private RestService restService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PetRepository petRepository;

  @Value("${token}")
  private String clientToken;
  @Value("${template.adoption.name}")
  private String adoptionTemplate;

  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Transactional
  public void save(Command command, User adopter){
    TelephoneCommand telephoneCommand = (TelephoneCommand)command;
    Pet pet = petService.getPetByUuid(telephoneCommand.getUuid());
    pet.setStatus(PetStatus.ADOPTED);
    adopter.setMobile(telephoneCommand.getMobile());
    pet.setAdopter(adopter);
    petRepository.save(pet);
    userRepository.save(adopter);
    createAdoptionDataMessage(command, pet);
  }

  private void createAdoptionDataMessage(Command command, Pet pet){
    User owner = pet.getUser();
    User adopter = pet.getAdopter();
    MessageCommand messageCommand = new MessageCommand();
    messageCommand.setEmail(owner.getEmail());
    messageCommand.setName(pet.getName());
    StringBuilder sb = new StringBuilder();
    sb.append(adopter.getFirstname());
    sb.append(" ");
    sb.append(adopter.getLastname());
    messageCommand.setContactName(sb.toString());
    messageCommand.setEmailContact(adopter.getEmail());
    messageCommand.setMessage(adopter.getMobile());
    messageCommand.setToken(clientToken);
    messageCommand.setTemplate(adoptionTemplate);
    log.info("Command: " + messageCommand);
    try{
      restService.sendMessage(messageCommand);
    } catch (IOException ioe){
      throw new BusinessException(ioe.getMessage());
    }
  }

}