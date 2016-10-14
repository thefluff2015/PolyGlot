/*
 * Copyright (c) 2014-2015, Draque Thompson, draquemail@gmail.com
 * All rights reserved.
 *
 * Licensed under: Creative Commons Attribution-NonCommercial 4.0 International Public License
 * See LICENSE.TXT included with this code to read the full license agreement.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package PolyGlot.ManagersCollections;

import PolyGlot.Nodes.ConWord;
import PolyGlot.DictCore;
import PolyGlot.Nodes.DictNode;
import PolyGlot.PGTUtil;
import PolyGlot.Nodes.TypeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author draque
 */
public class TypeCollection extends DictionaryCollection {
    final DictCore core;

    public TypeNode getBufferType() {
        return (TypeNode) bufferNode;
    }
    
    @Override
    public void deleteNodeById(Integer _id) throws Exception {
        super.deleteNodeById(_id);
        
        // only push update if not core loading file
        if (!core.isCurLoading()) {
            core.pushUpdate();
        }
    }

    @Override
    public int addNode(DictNode _addType) throws Exception {
        bufferNode = new TypeNode();
        
        return super.addNode(_addType);
    }
    
    public TypeCollection(DictCore _core) {
        bufferNode = new TypeNode();
        core = _core;
}

    /**
     * Tests whether type based requirements met for word
     *
     * @param word word to check
     * @return empty if no problems, string with problem description otherwise
     */
    public String typeRequirementsMet(ConWord word) {
        String ret = "";

        TypeNode type = this.getNodeById(word.getWordTypeId());

        // all requirements met if no type set at all.
        if (type != null) {
            if (type.isDefMandatory() && word.getDefinition().equals("")) {
                ret = type.getValue() + " requires a definition.";
            } /*else if (type.isGenderMandatory() && word.getGender().equals("")) { // TODO: Replace with class requirements
                ret = type.getValue() + " requires a gender.";
            }*/ else if (type.isProcMandatory() && word.getPronunciation().equals("")) {
                ret = type.getValue() + " requires a pronunciation.";
            }
        }

        return ret;
    }
    
    /**
     * This is a method used for finding nodes by name. Only for use when loading
     * old PolyGlot files. DO NOT rely on names for uniqueness moving forward.
     * @param name name of part of speech to search for
     * @return matching part of speech. Throws error otherwise
     * @throws java.lang.Exception if not found
     */
    public TypeNode findByName(String name) throws Exception {
       TypeNode ret = null;
       
       for (Object n : nodeMap.values()) {
           TypeNode curNode = (TypeNode)n;
           if (curNode.getValue().toLowerCase().equals(name.toLowerCase())) {
               ret = curNode;
               break;
           }
       }
       
       if (ret == null) {
           throw new Exception("Unable to find part of speech: " + name);
       }
       return ret; 
    }

    /**
     * Finds/returns type (if extant) by name
     *
     * @param _name
     * @return found type node, null otherwise
     */
    public TypeNode findTypeByName(String _name) {
        TypeNode ret = null;

        if (!_name.equals("")) {
            Iterator<Entry<Integer, TypeNode>> it = nodeMap.entrySet().iterator();
            Entry<Integer, TypeNode> curEntry;

            while (it.hasNext()) {
                curEntry = it.next();

                if (curEntry.getValue().getValue().toLowerCase().equals(_name.toLowerCase())) {
                    ret = curEntry.getValue();
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * inserts current buffer word to conWord list based on id; blanks out
     * buffer
     *
     * @param _id
     * @return
     * @throws Exception
     */
    public Integer insert(Integer _id) throws Exception {
        Integer ret;

        TypeNode insWord = new TypeNode();
        insWord.setEqual(bufferNode);
        insWord.setId(_id);

        ret = super.insert(_id, bufferNode);

        bufferNode = new TypeNode();
        
        // only push update if not due to a core file load
        if (!core.isCurLoading()) {
            core.pushUpdate();
        }

        return ret;
    }

    /**
     * inserts current buffer to conWord list and generates id; blanks out
     * buffer
     *
     * @return ID of newly created node
     * @throws Exception
     */
    public Integer insert() throws Exception {
        Integer ret;

        ret = super.insert(bufferNode);

        bufferNode = new TypeNode();
        
        // only push update if not core loading file
        if (!core.isCurLoading()) {
            core.pushUpdate();
        }

        return ret;
    }

    @Override
    public TypeNode getNodeById(Integer _id) {
        TypeNode ret = null;
        
        try {
            return (TypeNode) super.getNodeById(_id);
        } catch (Exception e) {
            // Do nothing. Lack of ID should resuly in return of null.
        }
        
        return ret;
    }
    
    @Override
    public void clear() {
        bufferNode = new TypeNode();
    }

    /**
     * returns iterator of nodes with their IDs as the entry key (ordered)
     *
     * @return
     */
    public Iterator<TypeNode> getNodeIterator() {
        List<TypeNode> retList = new ArrayList<>(nodeMap.values());

        Collections.sort(retList);

        return retList.iterator();
    }

    public boolean nodeExists(String findType) {
        boolean ret = false;
        Iterator<Map.Entry<Integer, TypeNode>> searchList = nodeMap.entrySet()
                .iterator();

        while (searchList.hasNext()) {
            Map.Entry<Integer, TypeNode> curEntry = searchList.next();
            TypeNode curType = curEntry.getValue();

            if (curType.getValue().equals(findType)) {
                ret = true;
                break;
            }
        }

        return ret;
    }
    
    public boolean nodeExists(int id) {
        return nodeMap.containsKey(id);
    }
    
    public TypeNode findOrCreate(String name) throws Exception {
        TypeNode node = new TypeNode();
        node.setValue(name);
        return findOrCreate(node);
    }
    
    public TypeNode findOrCreate(TypeNode node) throws Exception {
        TypeNode ret = null;
        
        for (Object n : nodeMap.values()) {
            TypeNode compNode = (TypeNode)n;
            if (compNode.getValue().equals(node.getValue())
                    && compNode.getGloss().equals(node.getGloss())) {
                ret = compNode;
                break;
            }
        }
        
        if (ret == null) {
            ret = getNodeById(insert(node));
        }
        
        return ret;
    }
    
    /**
     * Writes all type information to XML document
     * @param doc Document to write to
     * @param rootElement root element of document
     */
    public void writeXML(Document doc, Element rootElement) {
        Iterator<TypeNode> typeLoop = getNodeIterator();
        Element wordNode;
        Element wordValue;
        
        while (typeLoop.hasNext()) {
            TypeNode curType = typeLoop.next();

            wordNode = doc.createElement(PGTUtil.typeXID);
            rootElement.appendChild(wordNode);

            wordValue = doc.createElement(PGTUtil.typeIdXID);
            Integer wordId = curType.getId();
            wordValue.appendChild(doc.createTextNode(wordId.toString()));
            wordNode.appendChild(wordValue);

            wordValue = doc.createElement(PGTUtil.typeNameXID);
            wordValue.appendChild(doc.createTextNode(curType.getValue()));
            wordNode.appendChild(wordValue);

            wordValue = doc.createElement(PGTUtil.typeNotesXID);
            wordValue.appendChild(doc.createTextNode(curType.getNotes()));
            wordNode.appendChild(wordValue);

            wordValue = doc.createElement(PGTUtil.typeDefManXID);
            wordValue.appendChild(doc.createTextNode(curType.isDefMandatory() ? "T" : "F"));
            wordNode.appendChild(wordValue);

            wordValue = doc.createElement(PGTUtil.typeGenderManXID);
            wordValue.appendChild(doc.createTextNode(curType.isGenderMandatory() ? "T" : "F"));
            wordNode.appendChild(wordValue);

            wordValue = doc.createElement(PGTUtil.typeProcManXID);
            wordValue.appendChild(doc.createTextNode(curType.isProcMandatory() ? "T" : "F"));
            wordNode.appendChild(wordValue);
            
            wordValue = doc.createElement(PGTUtil.typePatternXID);
            wordValue.appendChild(doc.createTextNode(curType.getPattern()));
            wordNode.appendChild(wordValue);
            
            wordValue = doc.createElement(PGTUtil.typeGlossXID);
            wordValue.appendChild(doc.createTextNode(curType.getGloss()));
            wordNode.appendChild(wordValue);
        }
    }
}
