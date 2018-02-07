/*
 * Virus App - © Jonathan Feenstra 2018.
 * Datum laatste versie: 7 februari 2018
 * Functionaliteit: Het weergeven, sorteren en filteren van viruslijsten uit 
 * tsv-bestanden van Virus-Host DB en het bepalen van de overlap tussen deze 
 * lijsten. Specifiek ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv
 */
package virusapplicatie;

/**
 * GUI-class met de aanroep van methoden bij het uitvoeren van bepaalde acties.
 *
 * @author Jonathan Feenstra
 * @since JDK 1.8
 * @version 1.0
 */
public class VirusGUI extends javax.swing.JFrame {

    /**
     * Creates new form VirusGUI
     */
    public VirusGUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sortingButtonGroup = new javax.swing.ButtonGroup();
        headerLabel = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        headerSeparator = new javax.swing.JSeparator();
        fileLabel = new javax.swing.JLabel();
        searchTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        classLabel = new javax.swing.JLabel();
        classComboBox = new javax.swing.JComboBox<>();
        hostLabel = new javax.swing.JLabel();
        hostComboBox1 = new javax.swing.JComboBox<>();
        hostComboBox2 = new javax.swing.JComboBox<>();
        sortPanel = new javax.swing.JPanel();
        idRadioButton = new javax.swing.JRadioButton();
        classRadioButton = new javax.swing.JRadioButton();
        hostsRadioButton = new javax.swing.JRadioButton();
        orderComboBox = new javax.swing.JComboBox<>();
        virusScrollPane1 = new javax.swing.JScrollPane();
        virusEditorPane1 = new javax.swing.JEditorPane() {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent evt) {
                int pos = viewToModel(evt.getPoint());
                if (pos >= 0) {
                    javax.swing.text.html.HTMLDocument hdoc = (javax.swing.text.html.HTMLDocument) getDocument();
                    javax.swing.text.Element e = hdoc.getCharacterElement(pos);
                    try {                
                        return VirusLogica.virusIdToVirusMap.get(Integer.parseInt(hdoc.getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset()))).getSoort();
                    } catch (Exception ex) {
                    }
                }
                return null;
            }
        };
        javax.swing.ToolTipManager.sharedInstance().registerComponent(virusEditorPane1);
        javax.swing.text.DefaultCaret virusCaret1 = (javax.swing.text.DefaultCaret) virusEditorPane1.getCaret();
        virusCaret1.setUpdatePolicy(javax.swing.text.DefaultCaret.NEVER_UPDATE);
        ;
        virusScrollPane2 = new javax.swing.JScrollPane();
        virusEditorPane2 = new javax.swing.JEditorPane() {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent evt) {
                int pos = viewToModel(evt.getPoint());
                if (pos >= 0) {
                    javax.swing.text.html.HTMLDocument hdoc = (javax.swing.text.html.HTMLDocument) getDocument();
                    javax.swing.text.Element e = hdoc.getCharacterElement(pos);
                    try {                
                        return VirusLogica.virusIdToVirusMap.get(Integer.parseInt(hdoc.getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset()))).getSoort();
                    } catch (Exception ex) {
                    }
                }
                return null;
            }
        };
        javax.swing.ToolTipManager.sharedInstance().registerComponent(virusEditorPane2);
        javax.swing.text.DefaultCaret virusCaret2 = (javax.swing.text.DefaultCaret) virusEditorPane2.getCaret();
        virusCaret2.setUpdatePolicy(javax.swing.text.DefaultCaret.NEVER_UPDATE);
        ;
        overlapScrollPane = new javax.swing.JScrollPane();
        overlapEditorPane = new javax.swing.JEditorPane() {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent evt) {
                int pos = viewToModel(evt.getPoint());
                if (pos >= 0) {
                    javax.swing.text.html.HTMLDocument hdoc = (javax.swing.text.html.HTMLDocument) getDocument();
                    javax.swing.text.Element e = hdoc.getCharacterElement(pos);
                    try {                
                        return VirusLogica.virusIdToVirusMap.get(Integer.parseInt(hdoc.getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset()))).getSoort();
                    } catch (Exception ex) {
                    }
                }
                return null;
            }
        };
        javax.swing.ToolTipManager.sharedInstance().registerComponent(overlapEditorPane);
        javax.swing.text.DefaultCaret overlapCaret = (javax.swing.text.DefaultCaret) overlapEditorPane.getCaret();
        overlapCaret.setUpdatePolicy(javax.swing.text.DefaultCaret.NEVER_UPDATE);
        ;
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        fileMenuSeparator = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        copyMenu = new javax.swing.JMenu();
        copyList1Item = new javax.swing.JMenuItem();
        copyList2Item = new javax.swing.JMenuItem();
        copyOverlapItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        jdocMenuItem = new javax.swing.JMenuItem();
        helpMenuSeparator = new javax.swing.JPopupMenu.Separator();
        dbMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Virus App - © Jonathan Feenstra");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/virusicon.png")).getImage());
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(750, 580));
        setResizable(false);

        headerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/header.png"))); // NOI18N
        headerLabel.setFocusable(false);
        headerLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(headerLabel, java.awt.BorderLayout.PAGE_START);

        fileLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        fileLabel.setText("File of URL");

        searchTextField.setText("ftp://ftp.genome.jp/pub/db/virushostdb/virushostdb.tsv");
        searchTextField.setMaximumSize(new java.awt.Dimension(500, 20));
        searchTextField.setMinimumSize(new java.awt.Dimension(500, 20));
        searchTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchTextFieldFocusLost(evt);
            }
        });
        searchTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTextFieldActionPerformed(evt);
            }
        });

        searchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/search.png"))); // NOI18N
        searchButton.setText("Search");
        searchButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        searchButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        classLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        classLabel.setText("Viral Classification");

        classComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(VirusLogica.CLASSIFICATIES));
        classComboBox.setMaximumSize(new java.awt.Dimension(591, 20));
        classComboBox.setMinimumSize(new java.awt.Dimension(591, 20));
        classComboBox.setName(""); // NOI18N
        classComboBox.setPreferredSize(new java.awt.Dimension(591, 25));
        classComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                classComboBoxItemStateChanged(evt);
            }
        });

        hostLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        hostLabel.setText("Host ID");

        hostComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Open een file of URL" }));
        hostComboBox1.setEnabled(false);
        hostComboBox1.setMaximumSize(new java.awt.Dimension(280, 20));
        hostComboBox1.setMinimumSize(new java.awt.Dimension(280, 20));
        hostComboBox1.setPreferredSize(new java.awt.Dimension(280, 25));
        hostComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                hostComboBox1ItemStateChanged(evt);
            }
        });

        hostComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Open een file of URL" }));
        hostComboBox2.setEnabled(false);
        hostComboBox2.setMaximumSize(new java.awt.Dimension(280, 20));
        hostComboBox2.setMinimumSize(new java.awt.Dimension(280, 20));
        hostComboBox2.setPreferredSize(new java.awt.Dimension(280, 25));
        hostComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                hostComboBox2ItemStateChanged(evt);
            }
        });

        sortPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Sortering"));

        sortingButtonGroup.add(idRadioButton);
        idRadioButton.setSelected(true);
        idRadioButton.setText("Id");
        idRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idRadioButtonActionPerformed(evt);
            }
        });

        sortingButtonGroup.add(classRadioButton);
        classRadioButton.setText("Classificatie");
        classRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classRadioButtonActionPerformed(evt);
            }
        });

        sortingButtonGroup.add(hostsRadioButton);
        hostsRadioButton.setText("Aantal hosts");
        hostsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hostsRadioButtonActionPerformed(evt);
            }
        });

        orderComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hoog - laag", "Laag - hoog" }));
        orderComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                orderComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout sortPanelLayout = new javax.swing.GroupLayout(sortPanel);
        sortPanel.setLayout(sortPanelLayout);
        sortPanelLayout.setHorizontalGroup(
            sortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sortPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(orderComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hostsRadioButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(classRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(idRadioButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        sortPanelLayout.setVerticalGroup(
            sortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sortPanelLayout.createSequentialGroup()
                .addComponent(idRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(classRadioButton)
                .addGap(11, 11, 11)
                .addComponent(hostsRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(orderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        virusScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Viruslijst 1"));
        virusScrollPane1.setMaximumSize(new java.awt.Dimension(280, 150));
        virusScrollPane1.setPreferredSize(new java.awt.Dimension(280, 150));

        virusEditorPane1.setEditable(false);
        virusEditorPane1.setContentType("text/html"); // NOI18N
        virusEditorPane1.setMaximumSize(new java.awt.Dimension(280, 150));
        virusEditorPane1.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                virusEditorPane1HyperlinkUpdate(evt);
            }
        });
        virusScrollPane1.setViewportView(virusEditorPane1);

        virusScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Viruslijst 2"));
        virusScrollPane2.setMaximumSize(new java.awt.Dimension(280, 150));
        virusScrollPane2.setPreferredSize(new java.awt.Dimension(280, 150));

        virusEditorPane2.setEditable(false);
        virusEditorPane2.setContentType("text/html"); // NOI18N
        virusEditorPane2.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                virusEditorPane2HyperlinkUpdate(evt);
            }
        });
        virusScrollPane2.setViewportView(virusEditorPane2);

        overlapScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Overeenkomst"));

        overlapEditorPane.setEditable(false);
        overlapEditorPane.setContentType("text/html"); // NOI18N
        overlapEditorPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                overlapEditorPaneHyperlinkUpdate(evt);
            }
        });
        overlapScrollPane.setViewportView(overlapEditorPane);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerSeparator)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(overlapScrollPane)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(fileLabel)
                                    .addComponent(classLabel))
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addComponent(hostLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addComponent(sortPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addComponent(searchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addComponent(hostComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(virusScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(hostComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(classComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(virusScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(312, 312, 312)))))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(headerSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileLabel)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(classLabel)
                    .addComponent(classComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hostComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(hostLabel)
                        .addComponent(hostComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(sortPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(virusScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(virusScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(overlapScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/open.png"))); // NOI18N
        openMenuItem.setText("Open");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/save.png"))); // NOI18N
        saveMenuItem.setText("Save");
        saveMenuItem.setEnabled(false);
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);
        fileMenu.add(fileMenuSeparator);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exit.png"))); // NOI18N
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        toolsMenu.setText("Tools");

        copyMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copy.png"))); // NOI18N
        copyMenu.setText("Copy viruslist");
        copyMenu.setEnabled(false);

        copyList1Item.setText("Viruslijst 1");
        copyList1Item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyList1ItemActionPerformed(evt);
            }
        });
        copyMenu.add(copyList1Item);

        copyList2Item.setText("Viruslijst 2");
        copyList2Item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyList2ItemActionPerformed(evt);
            }
        });
        copyMenu.add(copyList2Item);

        copyOverlapItem.setText("Overeenkomst");
        copyOverlapItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyOverlapItemActionPerformed(evt);
            }
        });
        copyMenu.add(copyOverlapItem);

        toolsMenu.add(copyMenu);

        menuBar.add(toolsMenu);

        helpMenu.setText("Help");

        jdocMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/help.png"))); // NOI18N
        jdocMenuItem.setText("View Javadoc");
        jdocMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jdocMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(jdocMenuItem);
        helpMenu.add(helpMenuSeparator);

        dbMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/database.png"))); // NOI18N
        dbMenuItem.setText("Visit Virus-Host DB");
        dbMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(dbMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        VirusFileHandler.loadFile(evt);
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void hostsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hostsRadioButtonActionPerformed
        Virus.sortMethod = 2;
        VirusLogica.sortLists();
        VirusUpdater.updateAllEditorPanes();
    }//GEN-LAST:event_hostsRadioButtonActionPerformed

    private void classRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classRadioButtonActionPerformed
        Virus.sortMethod = 1;
        VirusLogica.sortLists();
        VirusUpdater.updateAllEditorPanes();
    }//GEN-LAST:event_classRadioButtonActionPerformed

    private void idRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idRadioButtonActionPerformed
        Virus.sortMethod = 0;
        VirusLogica.sortLists();
        VirusUpdater.updateAllEditorPanes();
    }//GEN-LAST:event_idRadioButtonActionPerformed

    private void hostComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_hostComboBox2ItemStateChanged
        hostComboBox2.setToolTipText((String) hostComboBox2.getSelectedItem());
        VirusUpdater.updateLists();
        VirusUpdater.updateEditorPane(virusEditorPane2, VirusLogica.virusList2);
        VirusUpdater.updateEditorPane(overlapEditorPane, VirusLogica.overlapList);
    }//GEN-LAST:event_hostComboBox2ItemStateChanged

    private void hostComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_hostComboBox1ItemStateChanged
        hostComboBox1.setToolTipText((String) hostComboBox1.getSelectedItem());
        VirusUpdater.updateLists();
        VirusUpdater.updateEditorPane(virusEditorPane1, VirusLogica.virusList1);
        VirusUpdater.updateEditorPane(overlapEditorPane, VirusLogica.overlapList);
    }//GEN-LAST:event_hostComboBox1ItemStateChanged

    private void classComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_classComboBoxItemStateChanged
        VirusUpdater.updateLists();
        VirusUpdater.updateAllEditorPanes();
    }//GEN-LAST:event_classComboBoxItemStateChanged

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        VirusFileHandler.loadFile(evt);
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTextFieldActionPerformed
        VirusFileHandler.loadFile(evt);
    }//GEN-LAST:event_searchTextFieldActionPerformed

    private void searchTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchTextFieldFocusLost
        if (searchTextField.getText().isEmpty()) {
            searchTextField.setText("Search");
        }
    }//GEN-LAST:event_searchTextFieldFocusLost

    private void searchTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchTextFieldFocusGained
        if (searchTextField.getText().equals("Search")) {
            searchTextField.setText("");
        }
    }//GEN-LAST:event_searchTextFieldFocusGained

    private void virusEditorPane1HyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_virusEditorPane1HyperlinkUpdate
        VirusLogica.visitHyperlink(evt);
    }//GEN-LAST:event_virusEditorPane1HyperlinkUpdate

    private void virusEditorPane2HyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_virusEditorPane2HyperlinkUpdate
        VirusLogica.visitHyperlink(evt);
    }//GEN-LAST:event_virusEditorPane2HyperlinkUpdate

    private void overlapEditorPaneHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_overlapEditorPaneHyperlinkUpdate
        VirusLogica.visitHyperlink(evt);
    }//GEN-LAST:event_overlapEditorPaneHyperlinkUpdate

    private void copyList1ItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyList1ItemActionPerformed
        VirusLogica.copyList(evt);
    }//GEN-LAST:event_copyList1ItemActionPerformed

    private void copyList2ItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyList2ItemActionPerformed
        VirusLogica.copyList(evt);
    }//GEN-LAST:event_copyList2ItemActionPerformed

    private void copyOverlapItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyOverlapItemActionPerformed
        VirusLogica.copyList(evt);
    }//GEN-LAST:event_copyOverlapItemActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        VirusFileHandler.saveFile();
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void dbMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbMenuItemActionPerformed
        VirusLogica.visitHyperlink(evt);
    }//GEN-LAST:event_dbMenuItemActionPerformed

    private void jdocMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jdocMenuItemActionPerformed
        VirusLogica.visitHyperlink(evt);
    }//GEN-LAST:event_jdocMenuItemActionPerformed

    private void orderComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_orderComboBoxItemStateChanged
        if (orderComboBox.getSelectedIndex() == 0) {
            Virus.sortOrder = true;
        } else {
            Virus.sortOrder = false;
        }
        VirusLogica.sortLists();
        VirusUpdater.updateAllEditorPanes();
    }//GEN-LAST:event_orderComboBoxItemStateChanged

    /**
     * Maakt de GUI zichtbaar.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VirusGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VirusGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VirusGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VirusGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new VirusGUI().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    static javax.swing.JComboBox<String> classComboBox;
    private javax.swing.JLabel classLabel;
    private javax.swing.JRadioButton classRadioButton;
    static javax.swing.JMenuItem copyList1Item;
    static javax.swing.JMenuItem copyList2Item;
    static javax.swing.JMenu copyMenu;
    static javax.swing.JMenuItem copyOverlapItem;
    static javax.swing.JMenuItem dbMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JLabel fileLabel;
    static javax.swing.JMenu fileMenu;
    private javax.swing.JPopupMenu.Separator fileMenuSeparator;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JSeparator headerSeparator;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPopupMenu.Separator helpMenuSeparator;
    static javax.swing.JComboBox<String> hostComboBox1;
    static javax.swing.JComboBox<String> hostComboBox2;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JRadioButton hostsRadioButton;
    private javax.swing.JRadioButton idRadioButton;
    static javax.swing.JMenuItem jdocMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    static javax.swing.JMenuItem openMenuItem;
    private javax.swing.JComboBox<String> orderComboBox;
    static javax.swing.JEditorPane overlapEditorPane;
    static javax.swing.JScrollPane overlapScrollPane;
    static javax.swing.JMenuItem saveMenuItem;
    static javax.swing.JButton searchButton;
    static javax.swing.JTextField searchTextField;
    private javax.swing.JPanel sortPanel;
    private javax.swing.ButtonGroup sortingButtonGroup;
    private javax.swing.JMenu toolsMenu;
    static javax.swing.JEditorPane virusEditorPane1;
    static javax.swing.JEditorPane virusEditorPane2;
    static javax.swing.JScrollPane virusScrollPane1;
    static javax.swing.JScrollPane virusScrollPane2;
    // End of variables declaration//GEN-END:variables
}
