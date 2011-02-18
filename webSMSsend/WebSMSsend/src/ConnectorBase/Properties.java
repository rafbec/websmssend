/*
 *
 *
    Copyright 2011 redrocketracoon@googlemail.com
    This file is part of WebSMSsend.

    WebSMSsend is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    WebSMSsend is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with WebSMSsend.  If not, see <http://www.gnu.org/licenses/>.

 *
 *
 */

package ConnectorBase;

/**
 *
 * static collection of properties each connector can claim to have
 */
public final class Properties {
static final public int CAN_SEND_NAME_AS_SENDER = 2;
static final public int CAN_SEND_TO_MULTIPLE_RECEPIENTS = 4;
static final public int CAN_SIMULATE_SEND_PROCESS = 8;
static final public int CAN_ABORT_SEND_PROCESS_WHEN_NO_FREE_SMS_AVAILABLE = 16;

}
