/*
Copyright 2017 José Luis De la Cruz Morales joseluis.delacruz@gmail.com

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

package com.jos.dem.vetlog.unit

import org.springframework.validation.Errors


import com.jos.dem.vetlog.command.ChangePasswordCommand
import com.jos.dem.vetlog.validator.ChangePasswordValidator

import spock.lang.Specification

class ChangePasswordValidatorSpec extends Specification {

  ChangePasswordValidator validator = new ChangePasswordValidator()

  Errors errors = Mock(Errors)

  void "should not validate since passwords are not equals"(){
    given:"A user command"
      Command command = new ChangePasswordCommand(token:'token',password:'password', passwordConfirmation:'p4ssword')
    when:"We validate passwords"
      validator.validate(command, errors)
    then:"We expect valiation failed"
    1 * errors.rejectValue('password', 'user.error.password.equals')
  }

}
